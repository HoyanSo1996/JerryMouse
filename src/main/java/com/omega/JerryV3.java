package com.omega;

import com.omega.handler.RequestHandler;
import com.omega.servlet.CustomServlet;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class JerryV3
 * 通过xml和反射来初始化容器
 *
 * @author KennySo
 * @date 2023/12/21
 */
public class JerryV3 {

    public static final ConcurrentHashMap<String, CustomServlet> servletInstanceMapping = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, String> servletUrlMapping = new ConcurrentHashMap<>();

    /**
     * 对容器进行初始化
     * tips: 获取web.xml, 由于servlet是我们自定义的, 所以idea不会帮我们同步到target文件,
     *       需要我们手动复制进去, 放在classes文件夹下, 或者放在src的resource文件下让idea同步
     */
    public void init() {
        // 获取整个项目的根目录
        String path = JerryV3.class.getResource("/").getPath();

        // 使用dom4j读取xml文件
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(path + "web.xml"));
            // 获取根元素
            Element rootElement = document.getRootElement();
            // 获取根元素下面的所有元素
            List<Element> elementList = rootElement.elements();
            // 遍历并过滤
            for (Element element : elementList) {
                if ("servlet".equalsIgnoreCase(element.getName())) {
                    String servletName = element.element("servlet-name").getText();
                    String servletClass = element.element("servlet-class").getText().trim();
                    servletInstanceMapping.put(
                            servletName,
                            (CustomServlet) Class.forName(servletClass).newInstance());
                } else if ("servlet-mapping".equalsIgnoreCase(element.getName())) {
                    String servletName = element.element("servlet-name").getText();
                    String urlPattern = element.element("url-pattern").getText();
                    // 要使用url来找servlet-name
                    servletUrlMapping.put(urlPattern, servletName);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                // 开启线程
                new Thread(new RequestHandler(socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        JerryV3 jerryV3 = new JerryV3();
        jerryV3.init();
        jerryV3.run();
    }
}
