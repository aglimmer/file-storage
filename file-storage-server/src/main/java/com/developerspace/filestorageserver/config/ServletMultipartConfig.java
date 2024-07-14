package com.developerspace.filestorageserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Slf4j
@Configuration
public class ServletMultipartConfig {
    /**
     * spring.server.MaxFileSize=1MB
     **/
    @Value("${spring.server.maxFileSize}")
    private DataSize maxFileSize;
    /**
     * 指定单个文件最大尺寸，如果超过这个大小，后端将不会接受该请求
     * 前端会受到错误：Code	Details
     * 413
     * Undocumented
     * Error: Request Entity Too Large
     **/
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        log.info("MaxFileSize="+maxFileSize.toMegabytes());
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(maxFileSize); // 当个文件最大大小
        factory.setMaxRequestSize(maxFileSize); // 整个请求的最大大小
        return factory.createMultipartConfig();
    }
}