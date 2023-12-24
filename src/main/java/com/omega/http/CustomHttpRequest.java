package com.omega.http;

import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Class CustomHttpRequest
 * 这个类等价于原生的servlet中的HttpServletRequest
 *
 * @author KennySo
 * @date 2023/12/21
 */
@Data
public class CustomHttpRequest {

    private String method;
    private String uri;
    private HashMap<String, String[]> parameterMap = new HashMap<>();
    private HashMap<String, String> headerMap = new HashMap<>();

    private final InputStream inputStream;

    public CustomHttpRequest(InputStream inputStream) {
        this.inputStream = inputStream;
        init();
    }

    public void init() {
        try {
            BufferedReader bufferedReader = new BufferedReader(
                                            new InputStreamReader(this.inputStream, StandardCharsets.UTF_8));

            // 1.读取请求行
            String requestLine = bufferedReader.readLine();
            String[] requestLineArr = requestLine.split(" ");
            // 获取请求方法
            this.method = requestLineArr[0];
            // 获取请求路径
            int index = requestLineArr[1].indexOf("?");
            if (index == -1) {
                this.uri = requestLineArr[1];
            } else {
                this.uri = requestLineArr[1].substring(0, index);
                // 获取请求参数
                String parameterPairString = requestLineArr[1].substring(index + 1);
                if (!parameterPairString.isEmpty()) {
                    parseParameterPairString(parameterPairString);
                }
            }

            // 2.获取请求体
            String requestHeaderString;
            while ((requestHeaderString = bufferedReader.readLine()) != null) {
                if (requestHeaderString.isEmpty()) {
                    break;
                }
                this.parseRequestHeaderString(requestHeaderString);
            }

            /*
                这里读取requestBody必须同read来读, 不能使用readLine, 因为请求体内容后面没有接终止符,
                所以必须使用字符数组收集信息
             */
            if ("POST".equalsIgnoreCase(this.method)) {
                int contentLength = Integer.parseInt(this.headerMap.get("Content-Length"));
                char[] buffer = new char[contentLength];
                int len = bufferedReader.read(buffer);
                String requestBody = new String(buffer, 0, len);
                this.parseParameterPairString(requestBody);
            }

            // !!这里不能关闭inputStream, 因为与socket关联, 如果在这里关了会报socketClose异常
            // inputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将请求参数字符串解析成Map结构, 并放入parameterMap中
     * @param parameterPairString 请求参数字符串 xxx=xxx
     */
    public void parseParameterPairString(String parameterPairString) {
        String[] parameterPairArr = parameterPairString.split("&");
        for (String parameterPair : parameterPairArr) {
            String key = parameterPair.split("=")[0];
            String value = parameterPair.split("=")[1];
            // 判断parameterMap列表里是否存在参数名,如果有说明传入的参数值是数组
            String[] oldValue = parameterMap.containsKey(key) ? parameterMap.get(key) : new String[0];
            String[] newValue = new String[oldValue.length + 1];
            System.arraycopy(oldValue, 0, newValue, 0, oldValue.length);
            newValue[newValue.length - 1] = value;
            parameterMap.put(key, newValue);
        }
    }

    /**
     * 将请求头参数字符串解析成Map结构, 并放入headerMap中
     * @param requestHeaderString 请求头参数 xxx: xxx
     */
    public void parseRequestHeaderString(String requestHeaderString) {
        int index = requestHeaderString.indexOf(":");
        String key = requestHeaderString.substring(0, index);
        String value = requestHeaderString.substring(index + 2);
        headerMap.put(key, value);
    }


    public String getParameter(String name) {
        if (parameterMap.containsKey(name)) {
            return parameterMap.get(name)[0];
        }
        return "";
    }

    public String[] getParameterValues(String name) {
        if (parameterMap.containsKey(name)) {
            return parameterMap.get(name);
        }
        return new String[0];
    }
}
