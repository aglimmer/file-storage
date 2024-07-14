package com.developerspace.filestorageserver.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;


@Slf4j
@AllArgsConstructor
@Component
public class AppInfoRunner implements CommandLineRunner {
    private Environment environment;
    private LocalFileProperties localFileProperties;
    private ServerProperties serverProperties;
    /**
     * 请勿在 CommandLineRunner 注入 HttpServletRequest，
     * 因为 CommandLineRunner 主要用于在应用程序启动后执行一些初始化操作，通常不应该依赖于与 Web 请求相关的对象如 HttpServletRequest
     * Caused by: java.lang.IllegalStateException: No thread-bound request found: Are you referring to request attributes outside of an actual web request, or processing a request outside of the originally receiving thread? If you are actually operating within a web request and still receive this message, your code is probably running outside of DispatcherServlet: In this case, use RequestContextListener or RequestContextFilter to expose the current request.
     */
    // private HttpServletRequest request;

    @Override
    public void run(String... args) throws Exception {
        log.info("args:{}",Arrays.deepToString(args));
        log.info("spring.profiles.active:{}",environment.getProperty("spring.profiles.active"));
        log.info("user.dir:{}",environment.getProperty("user.dir"));
        String serverAddress = environment.getProperty("server.address");
        // 没有配置则输出为null
        log.info("serverAddress:{}",serverAddress);
        Integer port = Integer.valueOf(environment.getProperty("server.port"));
        log.info("port:{}",port);
        log.info("localFile.url:{}",environment.getProperty("localFile.baseUrl"));
         log.info("localFileInfo:{}",localFileProperties.toString());

        // 获取失败
        // if(serverProperties==null){
        //     log.info("serverProperties is null");
        // }else {
        //     // log.info("serverProperties:{}", serverProperties);
        //     log.info("server port:{}",serverProperties.getPort());
        //     InetAddress address = serverProperties.getAddress();
        //     if(address==null){
        //         log.info("address is null");
        //     }else{
        //         log.info("hostName:{}",address.getHostName(),"port:{}",serverProperties.getPort());
        //     }
        // }
    }

}
