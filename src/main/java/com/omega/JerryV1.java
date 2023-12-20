package com.omega;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Class JerryV1
 *
 * @author KennySo
 * @version 1.0
 * @date 2023/12/20
 */
public class JerryV1 {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                // 读取request
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                                                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                System.out.println("\n=========接收到浏览器发送的数据==========");
                String msg;
                while ((msg = bufferedReader.readLine()) != null) {
                    if (msg.isEmpty()) {
                        break;
                    }
                    System.out.println(msg);
                }

                // 返回response
                OutputStream outputStream = socket.getOutputStream();
                String responseHeader = "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: text/html; charset=utf-8\r\n" +
                                        "\r\n";
                String responseBody = "<h1>Hi, Jerry mouse!</h1>";
                String response = responseHeader + responseBody;
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                // 关闭资源
                outputStream.close();
                inputStream.close();
                socket.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
