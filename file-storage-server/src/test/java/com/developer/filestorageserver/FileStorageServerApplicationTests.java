package com.developer.filestorageserver;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

@Slf4j
//@SpringBootTest
class FileStorageServerApplicationTests {

	@Test
	void contextLoads() {
		try {
			log.info(1 / 0);
			String aaa=  "0";
			aaa.substring(10,12);
		}catch (ArithmeticException e){
			log.info(e.getMessage());
			throw new ArithmeticException("除数为0异常哈哈哈");
		}catch (Exception  e){
			log.info("Exception----------");
		}
		log.info("----end----");
	}
	@Test
	void intTest(){
		String fileUrl = "xxx.xxxjpg";
		String fileSuffix = "";
		if(fileUrl.contains(".")){
			String suffix = fileUrl.substring(fileUrl.lastIndexOf(".")+1).toLowerCase();
			if(suffix.length()>0  && suffix.length()<5){
				fileSuffix = suffix;
			}
		}
		if(Objects.equals(fileSuffix,"")){
			fileSuffix = "download";
		}
		log.info(fileSuffix);

	}


	/**
	 * 判断是不是符合邮箱的格式
	 **/
	public static boolean isEmail(String email) {
		String regex = "\\w+@\\w+(\\.\\w{2,3})*\\.\\w{2,3}";
		return email.matches(regex);
	}

	/**
	 * 必须为数字字母组合
	 **/
	public static boolean isValidPassword(String password){
		String str = password.replace(" ","");
		boolean isValid = str.matches("\\S*[\\d]+[a-zA-Z]+\\S*") && str.length()>=8;
		return isValid;
	}


}
