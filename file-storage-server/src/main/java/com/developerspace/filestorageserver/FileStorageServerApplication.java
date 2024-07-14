package com.developerspace.filestorageserver;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.util.ResourceUtils;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.developerspace"})
public class FileStorageServerApplication {

	@SneakyThrows
	public static void main(String[] args) {
		// Resource directory: D:\User\IDEA\file-storage
		// String rootPath = System.getProperty("user.dir");
		// log.info("Resource directory: " + rootPath);
		// String resourcePath = ResourceUtils.getURL("classpath:").getPath();
		// D:/User/IDEA/file-storage/file-storage-server/target/classes/
		// log.info("Resource directory: " + resourcePath);
		SpringApplication.run(FileStorageServerApplication.class, args);
	}

}
