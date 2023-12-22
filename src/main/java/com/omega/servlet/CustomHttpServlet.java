package com.omega.servlet;

import com.omega.http.CustomHttpRequest;
import com.omega.http.CustomHttpResponse;

import java.io.IOException;

/**
 * Class CustomHttpServlet
 *
 * @author KennySo
 * @date 2023/12/21
 */
public abstract class CustomHttpServlet implements CustomServlet {

    @Override
    public void service(CustomHttpRequest request, CustomHttpResponse response) throws IOException {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            this.doGet(request, response);
        } else if ("POST".equalsIgnoreCase(request.getMethod())) {
            this.doPost(request, response);
        }
    }

    /*
        这里使用了模板设计模式
        让 CustomHttpServlet 的子类去实现 doGet 和 doPost 方法
     */
    public abstract void doGet(CustomHttpRequest request, CustomHttpResponse response);

    public abstract void doPost(CustomHttpRequest request, CustomHttpResponse response);
}
