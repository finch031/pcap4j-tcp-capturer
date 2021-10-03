package com.github.capture;

import com.alibaba.fastjson.JSON;
import com.github.capture.model.TcpPacketOuterClass;
import com.github.capture.sender.*;
import com.google.protobuf.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

public class RawPacketCapture {
    private static final Logger LOG = LogManager.getLogger(RawPacketCapture.class);
    private static final int timeoutMillis = 100;
    private static final int bufferSize = 1024 * 1024;

    public static void main(String[] args) throws PcapNativeException,NotOpenException{
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

        handle.setFilter("tcp", BpfProgram.BpfCompileMode.OPTIMIZE);

        long counter = 0;


        StandardSocketFactory standardSocketFactory = new StandardSocketFactory();
        XORShiftRandom random = new XORShiftRandom();
        long pid = JvmPid.getPid();

        Socket socket = null;

        try{
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

        while (true) {

            try{
                Packet packet = handle.getNextPacket();
                if (packet != null) {
                    int dlt = handle.getDlt().value();
                    Timestamp timestamp = handle.getTimestamp();
                    int packetLength = packet.length();

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
                        String messageId = Utils.formatDateTime(System.currentTimeMillis(),"yyyyMMddHHmmss") + "_" + randomId;

                        byte[] messageData = tcpPacket.toByteArray();
                        MessageRequestPacket messageRequestPacket = new MessageRequestPacket(clientId,messageTs,messageId,messageData);

                        byte[] body = JSON.toJSONBytes(messageRequestPacket);
                        ByteBuffer byteBuf = ByteBuffer.allocate(7 + body.length);

                        // 写消息头
                        byteBuf.putShort((short) 0x2323);
                        byteBuf.put((byte)0x04);
                        byteBuf.put((byte)0x01);
                        byteBuf.put((byte)0x01);
                        byteBuf.putShort((short) body.length);

                        // 写消息体
                        byteBuf.put(body);

                        // socket数据发送.
                        writeStream.write(byteBuf.array());

                        Utils.sleepQuietly(100);

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
            }catch (NotOpenException|IOException ex){
                String errorMsg = stackTrace(ex);
                LOG.error(errorMsg);
            }

        } // end of while

        // handle.close();
    }

    /**
     * get the stack trace from an exception as a string
     */
    public static String stackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
