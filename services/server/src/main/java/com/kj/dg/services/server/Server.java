package com.kj.dg.services.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jkong on 6/23/18.
 */
public class Server {
    public static final int PORT = 12345;//监听的端口号

    public static void main(String[] args) {

        System.out.println("Start server ...\n");
        Server server = new Server();
        server.init();
    }

    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // 一旦有堵塞, 则表示服务器与客户端获得了连接
                Socket client = serverSocket.accept();
                // 处理这次连接
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("Server failed : " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable {
        private Socket socket;

        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public void run() {
            try {
                // 读取客户端数据
                DataInputStream input = new DataInputStream(socket.getInputStream());
                String clientInputStr = input.readUTF(); //这里要注意和客户端输出流的写方法对应,否则会抛 EOFException
                // 处理客户端数据
                System.out.println("CMD from client : " + clientInputStr);
                // Manage k8s kubctl
                String response = runShellCmd(parseCmd(clientInputStr));
                // 向客户端回复信息
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                //System.out.print("请输入:\t");
                // 发送键盘输入的一行
                //String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
                //out.writeUTF(s);
                out.writeUTF(response);
                out.close();
                input.close();
            } catch (Exception e) {
                System.out.println("Server run exceptionally: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("Server finally failed: " + e.getMessage());
                    }
                }
            }
        }
    }

    public String parseCmd(String cmd){
        // check valid
        return cmd;
    }
    public String runShellCmd(String cmd){
        String s = null;
        try {
            // run the Unix "ps -ef" command
            //Process p = Runtime.getRuntime().exec("ps -ef");
            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            return cmd + " is done!";
            //System.exit(0);
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            return  cmd + " failed!";
            //System.exit(-1);
        }
    }
}
