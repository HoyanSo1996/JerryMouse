package com.omega.servlet;

import com.omega.http.CustomHttpRequest;
import com.omega.http.CustomHttpResponse;
import com.omega.utils.CalculateUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Class CalServlet
 *
 * @author KennySo
 * @date 2023/12/21
 */
public class CalServlet extends CustomHttpServlet {

    @Override
    public void doGet(CustomHttpRequest request, CustomHttpResponse response) {
        int num1 = CalculateUtil.parseValue(request.getParameter("num1"), 0);
        int num2 = CalculateUtil.parseValue(request.getParameter("num2"), 0);
        int sum = num1 + num2;

        OutputStream outputStream = response.getOutputStream();
        String responseMsg = CustomHttpResponse.RESPONSE_HEADER +
                             "<h1>" + num1 + " + " + num2 + " = " + sum + "</h1>";
        try {
            outputStream.write(responseMsg.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(CustomHttpRequest request, CustomHttpResponse response) {
        this.doGet(request, response);
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
