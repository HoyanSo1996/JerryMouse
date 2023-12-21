package com.omega.handler;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Class HttpHandler
 *
 * @author KennySo
 * @date 2023/12/21
 */
public class RequestHandler implements Runnable {

    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
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
            // 返回的http响应的响应头和响应体之间有两个换行 \r\n\r\n
            String responseHeader = "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: text/html; charset=utf-8\r\n" +
                                    "\r\n";
            String responseBody = "<h1>Hi, Jerry mouse!</h1>";
            String response = responseHeader + responseBody;
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            System.out.println("\n=========服务器返回给浏览器的信息==========");
            System.out.println(response);

            // 关闭资源
            outputStream.close();
            inputStream.close();
            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            // 保证socket一定关闭
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
