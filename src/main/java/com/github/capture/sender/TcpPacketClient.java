package com.github.capture.sender;


import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * @author yusheng
 * @version 1.0.0
 * @datetime 2021-09-07 10:19
 * @description tcp packet client.
 */
public class TcpPacketClient {
    public static void main(String[] args){
        if(args.length != 4){
            System.err.println("args error: host port packet_num client_number");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int packetNum = Integer.parseInt(args[2]);
        String clientNumber = args[3];

        StandardSocketFactory standardSocketFactory = new StandardSocketFactory();
        XORShiftRandom random = new XORShiftRandom();
        long pid = JvmPid.getPid();

        Socket socket = null;
        try{
            socket = standardSocketFactory.createSocket(host,port);
            socket.setTcpNoDelay(false);

            String clientId = socket.getLocalAddress().getHostName() + "_" + clientNumber + "_" + pid + "_" + Thread.currentThread().getId();

            OutputStream writeStream = socket.getOutputStream();

            for(int i = 0; i < packetNum; i++){
                int randomId = random.next(8);
                long messageTs = System.currentTimeMillis();
                String messageId = Utils.formatDateTime(System.currentTimeMillis(),"yyyyMMddHHmmss") + "_" + randomId;
                String message = System.currentTimeMillis() + "-" + i;

                MessageRequestPacket messageRequestPacket = new MessageRequestPacket(clientId,messageTs,messageId,message.getBytes());

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

                Thread.sleep(10);
            }
        }catch (IOException | InterruptedException ex){
            ex.printStackTrace();
        }finally {
            if(socket != null){
                try{
                    socket.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
    }
}