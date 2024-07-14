package com.developerspace.filestorageserver.config;


import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

@ToString
@Slf4j
@Component
@ConfigurationProperties(prefix = "local-file")
public class LocalFileProperties implements EnvironmentAware {
    private Integer maxSize;
    private String path;
    private String baseUrl;

    private Environment environment;

    public LocalFileProperties(){
        log.info("init LocalFileProperties...");
    }

    public Integer getMaxSize() {
        if(maxSize==null){
            maxSize = 1024*1024*10;
            log.info("默认上传大小：{}",maxSize);
        }
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public String getPath() {
        if(path==null || Objects.equals(path,"") || Objects.equals("default",path)){
            path = System.getProperty("user.dir")+"/upload";
            log.info("文件上传使用默认路径：{}",path);
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 获取文件请求前缀
    public String getBaseUrl() {
        if(baseUrl==null || Objects.equals("",baseUrl)){
            String serverAddress = environment.getProperty("server.address");
            log.info("serverAddress:{}",serverAddress);
            if(serverAddress==null || serverAddress.isEmpty()){
                baseUrl = "http://"+getCurrentIPAddress()+":"+environment.getProperty("server.port")+"/file";
            }
            Integer port = Integer.valueOf(environment.getProperty("server.port"));
            log.info("port:{}",port);
        }
        return baseUrl;
    }


    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public  String getCurrentIPAddress() {
        String ip = null;
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                // log.info("isLoopBack:{}",netInterface.isLoopback());
                // log.info("isVirtual:{}",netInterface.isVirtual());
                // log.info("isUp:{}",netInterface.isUp());
                // log.info("displayName:{}",netInterface.getDisplayName());
                // 去除回环接口，子接口，未运行和接口
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }
                if (!netInterface.getDisplayName().contains("Intel") && !netInterface.getDisplayName().contains("Realtek")) {
                    continue;
                }
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) addresses.nextElement();

                    if (inetAddress != null) {
                        // log.info("hostAddress:{}",inetAddress.getHostAddress());
                        if (inetAddress instanceof Inet4Address) {
                            // log.info("address:{}",inetAddress.getHostAddress());
                            ip = inetAddress.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        if(ip==null){
            ip = "127.0.0.1";
        }
        return ip;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
