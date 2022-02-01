package com.github;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author yusheng
 * @version 1.0.0
 * @datetime 2022-01-25 17:35
 * @description
 */
public class Main {

    public static void main(String[] args){

        try {
            ServerSocket ss = new ServerSocket(8888);
            System.out.println("启动服务器....");

            while(true){
                Socket s = ss.accept();
                System.out.println("客户端:"+s.getInetAddress().getLocalHost()+"已连接到服务器");

                try{
                    Thread.sleep(1000);
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
