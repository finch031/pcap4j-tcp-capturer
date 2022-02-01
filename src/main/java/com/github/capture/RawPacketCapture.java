package com.github.capture;

import com.alibaba.fastjson.JSON;
import com.github.capture.model.TcpPacketOuterClass;
import com.github.capture.sender.*;
import com.github.conf.AppConfiguration;
import com.google.protobuf.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.DataLinkType;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

public class RawPacketCapture {
    private static final Logger LOG = LogManager.getLogger(RawPacketCapture.class);
    private static final int timeoutMillis = 100;
    private static final int bufferSize = 1024 * 1024;

    public static void main(String[] args) throws PcapNativeException,NotOpenException{
        AppConfiguration configuration = AppConfiguration.loadFromPropertiesResource("client.properties");

        String NIF_NAME = null;

        if(args.length < 2){
            System.err.println("args error: host port [nif_name]");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        if(args.length == 3){
            NIF_NAME = args[2];
        }

        PcapNetworkInterface nif;
        if (NIF_NAME != null) {
            nif = Pcaps.getDevByName(NIF_NAME);
        } else {
            try {
                nif = new NifSelector().selectNetworkInterface();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        if (nif == null) {
            return;
        }

        System.out.println(nif.getName() + " (" + nif.getDescription() + ")");
        for (PcapAddress addr : nif.getAddresses()) {
            if (addr.getAddress() != null) {
                System.out.println("IP address: " + addr.getAddress());
            }
        }
        System.out.println();

        PcapHandle handle =
                new PcapHandle.Builder(nif.getName())
                        .snaplen(65535)
                        .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                        .timeoutMillis(timeoutMillis)
                        .bufferSize(bufferSize)
                        .build();

        String packetBpfExpression = configuration.getString("client.packet.bpf.expression","");
        if(packetBpfExpression != null && !packetBpfExpression.isEmpty()){
            handle.setFilter(packetBpfExpression, BpfProgram.BpfCompileMode.OPTIMIZE);
        }

        handle.setDlt(DataLinkType.EN10MB);

        long counter = 0;

        StandardSocketFactory standardSocketFactory = new StandardSocketFactory();
        XORShiftRandom random = new XORShiftRandom();
        long pid = JvmPid.getPid();

        Socket socket = null;

        try{
            // socket连接到数据接收服务DRS
            socket = standardSocketFactory.createSocket(host,port);
            socket.setTcpNoDelay(false);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        if(socket == null){
            System.err.println("can't connect to data receive service server!");
            System.exit(1);
        }

        String clientNumber = "c-" + random.nextInt(10);
        // netty请求客户端ID(主机名-随机号-进程id-线程id)
        String clientId = socket.getLocalAddress().getHostName() + "_" + clientNumber + "_" + pid + "_" + Thread.currentThread().getId();

        OutputStream writeStream = null;

        try{
            writeStream = socket.getOutputStream();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        if(writeStream == null){
            System.err.println("can't get output stream to data receive service server!");
            System.exit(1);
        }

        int captureSleepMills = configuration.getInteger("client.capture.sleep.mills",100);

        while (true) {

            try{
                Packet packet = handle.getNextPacket();
                if (packet != null) {
                    int dlt = handle.getDlt().value();
                    Timestamp timestamp = handle.getTimestamp();
                    int packetLength = packet.length();

                    // debug(packet);

                    // Ethernet
                    if(dlt == 1) {
                        byte[] rawData = packet.getRawData();

                        TcpPacketOuterClass.TcpPacket tcpPacket = TcpPacketOuterClass.TcpPacket
                                .newBuilder()
                                .setTs(timestamp.getTime())
                                .setNanos(timestamp.getNanos())
                                .setPacketLen(packetLength)
                                .setRawData(ByteString.copyFrom(rawData))
                                .build();

                        int randomId = random.next(8);
                        long messageTs = System.currentTimeMillis();
                        // 消息ID
                        String messageId = Utils.formatDateTime(System.currentTimeMillis(),"yyyyMMddHHmmss") + "_" + randomId;
                        // 消息体数据
                        byte[] messageData = tcpPacket.toByteArray();
                        // netty请求数据包
                        MessageRequestPacket messageRequestPacket = new MessageRequestPacket(clientId,messageTs,messageId,messageData);

                        byte[] body = JSON.toJSONBytes(messageRequestPacket);
                        ByteBuffer byteBuf = ByteBuffer.allocate(10 + body.length);

                        // 写消息头
                        byteBuf.putShort((short) 0x2323);

                        // 指令类型
                        byteBuf.put((byte)0x04);

                        // 消息版本
                        byteBuf.put((byte)0x01);

                        // 数据加密方式(1-非加密,2-AES128加密)
                        byteBuf.put((byte)0x01);

                        // 数据压缩方式(0-不压缩，1-zstd压缩)
                        byteBuf.put((byte)0x00);

                        // 报文原始大小
                        byteBuf.putShort((short)body.length);

                        // 报文发出大小
                        byteBuf.putShort((short) body.length);

                        // 写消息体
                        byteBuf.put(body);

                        // socket数据发送.
                        writeStream.write(byteBuf.array());

                        if(captureSleepMills > 0){
                            Utils.sleepQuietly(captureSleepMills);
                        }

                    /*
                    EthernetPacket ethernetPacket = EthernetPacket.newPacket(packet.getRawData(), 0, packet.getRawData().length);
                    if(ethernetPacket.getHeader().getType() == EtherType.IPV4){
                        IpV4Packet ipV4Packet = IpV4Packet.newPacket(ethernetPacket.getRawData(), 0, ethernetPacket.getRawData().length);
                        System.out.println(ipV4Packet.toString());
                        TcpPacket tcpPacket = packet.get(TcpPacket.class);
                        System.out.println(tcpPacket.toString());
                    }
                    */
                    }

                    counter++;
                    if(counter % 10000 == 0){
                        LOG.info("capture packet counter:" + counter);
                    }
                }
            }catch (NotOpenException ex){
                String errorMsg = Utils.stackTrace(ex);
                LOG.error(errorMsg);
            } catch (IOException ex){

                // socket重连
                reConnection(socket,standardSocketFactory,writeStream,host,port);
                // 重置客户端id
                clientId = socket.getLocalAddress().getHostName() + "_" + "c-" + random.nextInt(10) + "_" + pid + "_" + Thread.currentThread().getId();
                LOG.error("new client id:{}",clientId);

                /*
                // socket连接已关闭
                if(socket.isClosed()){
                    // socket重连
                    reConnection(socket,standardSocketFactory,writeStream,host,port);
                    // 重置客户端id
                    clientId = socket.getLocalAddress().getHostName() + "_" + "c-" + random.nextInt(10) + "_" + pid + "_" + Thread.currentThread().getId();
                    LOG.error("new client id:{}",clientId);
                }else{
                    // socket连接未关闭
                    if(socket.isConnected()){
                        String errorMsg = Utils.stackTrace(ex);
                        LOG.error("socket is connected,but error occure: " + errorMsg);
                        // 获取新的输出流通道
                        getNewWriteStream(socket,writeStream);
                    }else{
                        String errorMsg = Utils.stackTrace(ex);
                        LOG.error("socket is not closed and is not connected,but error occure:{} , now reconnection...", errorMsg);

                        // socket重连
                        reConnection(socket,standardSocketFactory,writeStream,host,port);
                        // 重置客户端id
                        clientId = socket.getLocalAddress().getHostName() + "_" + "c-" + random.nextInt(10) + "_" + pid + "_" + Thread.currentThread().getId();
                        LOG.error("new client id:{}",clientId);
                    }
                }
                */

            }


        } // end of while

        // handle.close();
    }

    private static void reConnection(Socket socket,
                                     StandardSocketFactory standardSocketFactory,
                                     OutputStream writeStream,
                                     String host,
                                     int port){
        // 关闭输出流通道
        Utils.closeOutputStream(writeStream);
        // 关闭socket连接
        Utils.closeSocket(socket);
        // 睡眠5秒
        Utils.sleepQuietly(5 * 1000);

        // 重新建立连接
        try{
            socket = standardSocketFactory.createSocket(host,port);
            socket.setTcpNoDelay(false);
            writeStream = socket.getOutputStream();
        }catch (IOException ioe){
            String errorMsg = Utils.stackTrace(ioe);
            LOG.error("reconnection error: " + errorMsg);
        }
    }

    private static void getNewWriteStream(Socket socket,OutputStream writeStream){
        // 关闭输出流通道
        Utils.closeOutputStream(writeStream);
        // 重新建立连接
        try{
            writeStream = socket.getOutputStream();
        }catch (IOException ioe){
            String errorMsg = Utils.stackTrace(ioe);
            LOG.error("get new write stream error: " + errorMsg);
        }
    }

    private static void debug(Packet packet){
        EthernetPacket ethernetPacket = packet.get(EthernetPacket.class);
        EthernetPacket.EthernetHeader ethernetHeader = ethernetPacket.getHeader();
        System.out.println(ethernetHeader.getType().name());

        if(ethernetHeader.getType() == EtherType.IPV4){
            IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
            System.out.println(ipV4Packet.getHeader());
        }
    }
}
