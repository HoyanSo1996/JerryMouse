package com.omega.handler;

import com.omega.JerryV3;
import com.omega.http.CustomHttpRequest;
import com.omega.http.CustomHttpResponse;
import com.omega.servlet.CustomServlet;
import com.omega.utils.CommonUtil;

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
    private final CustomHttpRequest customHttpRequest;
    private final CustomHttpResponse customHttpResponse;


    public RequestHandler(Socket socket) {
        this.socket = socket;
        try {
            // 使用自定义的CustomHttpServlet来处理http请求和响应
            this.customHttpRequest = new CustomHttpRequest(socket.getInputStream());
            this.customHttpResponse = new CustomHttpResponse(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            // 获取uri, 判断请求的是动态资源还是静态资源
            String uri = customHttpRequest.getUri();
            String[] urlArr = uri.split("\\.");
            String postfix = urlArr[urlArr.length - 1];
            if (CommonUtil.isStaticResource(postfix)) {
                // 静态资源
                handleStaticResource(uri, postfix);
            } else {
                // 动态资源servlet
                handleDynamicResource(uri);
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


    /**
     * 处理静态资源
     * @param uri uri
     * @param resourcePattern 资源类型
     */
    public void handleStaticResource(String uri, String resourcePattern) throws IOException {
        String path = RequestHandler.class.getResource("/").getPath();
        File file = new File(path + uri);
        // 判断资源是否存在
        if (file.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            StringBuilder response = new StringBuilder();
            String line;
            response.append(CustomHttpResponse.RESPONSE_HEADER);
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line).append("\r\n");
            }
            String str = new String(response);
            OutputStream outputStream = this.customHttpResponse.getOutputStream();
            outputStream.write(str.getBytes());
            outputStream.flush();
            outputStream.close();

        } else {
            this.respondNotFoundError(customHttpResponse.getOutputStream());
        }

    }

    /**
     * 处理动态资源
     * @param uri uri
     */
    public void handleDynamicResource(String uri) throws IOException {
        String servletName = JerryV3.servletUrlMapping.get(uri);
        if (servletName != null) {
            CustomServlet customServlet = JerryV3.servletInstanceMapping.get(servletName);
            customServlet.service(customHttpRequest, customHttpResponse);
        } else {
            this.respondNotFoundError(customHttpResponse.getOutputStream());
        }
    }

    public void respondNotFoundError(OutputStream outputStream) throws IOException {
        String response = CustomHttpResponse.RESPONSE_HEADER + "<h1>Not Found</h1>";
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
