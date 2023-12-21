package com.omega.http;

import lombok.Data;

import java.io.OutputStream;

/**
 * Class CustomHttpResponse
 * 这个类等价于原生的servlet中的HttpServletResponse
 * 可以封装outPutStream, 即可以通过它返回HTTP响应给浏览器/客户端
 *
 * @author KennySo
 * @date 2023/12/21
 */
@Data
public class CustomHttpResponse {

    // 返回的http响应的响应头和响应体之间有两个换行 \r\n\r\n
    public static final String RESPONSE_HEADER = "HTTP/1.1 200 OK\r\n" +
                                                  "Content-Type: text/html; charset=utf-8\r\n" +
                                                  "\r\n";;

    private final OutputStream outputStream;

    public CustomHttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

}
