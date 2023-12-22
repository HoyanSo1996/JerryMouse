package com.omega.handler;

import com.omega.http.CustomHttpRequest;
import com.omega.http.CustomHttpResponse;
import com.omega.servlet.CalServlet;

import java.io.IOException;
import java.net.Socket;

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
            // 使用自定义的CustomHttpServlet来处理http请求和响应
            CustomHttpRequest customHttpRequest = new CustomHttpRequest(socket.getInputStream());
            CustomHttpResponse customHttpResponse = new CustomHttpResponse(socket.getOutputStream());
            CalServlet calServlet = new CalServlet();
            calServlet.service(customHttpRequest, customHttpResponse);

            // 关闭资源
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
