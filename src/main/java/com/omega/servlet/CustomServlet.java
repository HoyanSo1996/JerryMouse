package com.omega.servlet;

import com.omega.http.CustomHttpRequest;
import com.omega.http.CustomHttpResponse;

import java.io.IOException;

/**
 * Class CustomServlet
 *
 * @author KennySo
 * @date 2023/12/21
 */
public interface CustomServlet {

    void init() throws Exception;

    void service(CustomHttpRequest request, CustomHttpResponse response) throws IOException;

    void destroy();
}
