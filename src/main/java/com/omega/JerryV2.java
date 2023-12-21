package com.omega;

import com.omega.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class JerryV2
 *
 * @author KennySo
 * @date 2023/12/21
 */
public class JerryV2 {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                // 开启线程
                new Thread(new RequestHandler(socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
