package com.developerspace.filestorageapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"com.developerspace.filestoragesdk"})
@SpringBootApplication
public class FileStorageAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileStorageAppApplication.class, args);
	}

}
