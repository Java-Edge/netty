package io.netty.example.helloworld;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author JavaEdge
 * @date 2021/5/17
 */
public class TcpServer {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8089);
        while (true) {
            System.out.println("等待客户端连接...");
            Socket socket = serverSocket.accept();
            System.out.println("客户端连接成功..." + socket.getRemoteSocketAddress());
            new Thread(new Handler(socket)).start();
        }
    }

    public static class Handler implements Runnable {

        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            int readNum = 0;
            byte[] content = new byte[1024];
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                while ((readNum = in.read(content)) != -1) {
                    System.out.println(new String(Arrays.copyOf(content, readNum)));
                    out.write("hello client".getBytes());
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}