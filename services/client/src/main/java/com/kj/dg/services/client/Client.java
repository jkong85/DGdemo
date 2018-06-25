package com.kj.dg.services.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by jkong on 6/24/18.
 */
public class Client {
    public static final String IP_ADDR = "172.33.13.1";//server address
    public static final int PORT = 12345;// server port
    public static void main(String[] args) {
        //String IP_ADDR = args[0];
        //int PORT = Integer.parseInt(args[1]);// server port
        //System.out.println("Client is starting to connect to IP: " + IP_ADDR + " Port: " + args[1]);
        System.out.println("Client stop when recieved OK from server!\n");
        int flag = 100;  // For debug, avoiding unlimited exceptions

        while (true && flag-- > 0)
        {
            Socket socket = null;
            try {
                //创建一个流套接字并将其连接到指定主机上的指定端口号
                //System.out.println("Connect to IP: " + IP_ADDR + " Port: " + args[1]);
                socket = new Socket(IP_ADDR, PORT);

                //读取服务器端数据
                DataInputStream input = new DataInputStream(socket.getInputStream());
                //向服务器端发送数据
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                System.out.print("Please input : \t");
                String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
                str = "sudo kubectl get pods -o wide";
                out.writeUTF(str);

                String ret = input.readUTF();
                System.out.println("Server answer : " + ret);
                // 如接收到 "OK" 则断开连接
                if ("OK".equals(ret)) {
                    System.out.println("Client will be closed ");
                    Thread.sleep(10000);
                    break;
                }

                out.close();
                input.close();
            } catch (Exception e) {
                System.out.println("Client has exception:" + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        socket = null;
                        System.out.println("Client finally exception:" + e.getMessage());
                    }
                }
            }
        }
    }
}
