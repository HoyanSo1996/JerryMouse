package com.omega.handler;

import com.omega.JerryV3;
import com.omega.http.CustomHttpRequest;
import com.omega.http.CustomHttpResponse;
import com.omega.servlet.CustomServlet;

import java.io.IOException;
import java.io.OutputStream;
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
            // 使用自定义的CustomHttpServlet来处理http请求和响应
            CustomHttpRequest customHttpRequest = new CustomHttpRequest(socket.getInputStream());
            CustomHttpResponse customHttpResponse = new CustomHttpResponse(socket.getOutputStream());

            // 通过uri从容器中获取servlet实例
            String uri = customHttpRequest.getUri();
            String servletName = JerryV3.servletUrlMapping.get(uri);
            if (servletName != null) {
                CustomServlet customServlet = JerryV3.servletInstanceMapping.get(servletName);
                customServlet.service(customHttpRequest, customHttpResponse);
            } else {
                OutputStream outputStream = customHttpResponse.getOutputStream();
                String response = CustomHttpResponse.RESPONSE_HEADER + "<h1>Not Found</h1>";
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();
            }

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
