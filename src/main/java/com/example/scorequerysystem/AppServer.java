package com.example.scorequerysystem;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * 简易Java内置Http服务器示例，支持基本路由
 */
public class AppServer {

    public static void main(String[] args) throws IOException {
        // 创建HttpServer，监听端口8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 注册根路径处理器
        server.createContext("/", new RootHandler());

        // 开启服务器
        server.setExecutor(null); // 使用默认线程池
        server.start();

        System.out.println("服务器启动，监听端口8080");
    }

    // 根路径处理器
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html><body><h1>学生成绩查询系统</h1>" +
                              "<p>欢迎访问系统主页</p></body></html>";
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes("UTF-8"));
            os.close();
        }
    }
}
