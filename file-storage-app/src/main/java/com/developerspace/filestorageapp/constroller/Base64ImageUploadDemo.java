package com.developerspace.filestorageapp.constroller;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @Author wangz
 * @Date 2022-01-10 17:03
 */
@Slf4j
@Api(tags="上传文件内容为base64编码")
@RequestMapping("/app/demo/base64")
@RestController
public class Base64ImageUploadDemo {

	//本地图片保存的目录，应该在配置文件中指定然后注入，这里是为了演示方便
	private String localPath = "E:\\User\\IDEA\\image";
	//获取图片的URL前缀，这里一般在配置文件中指定然后注入

	/**
	 * 表单数据为base64编码的文件
	 * @param params
	 * @return
	 */
	@PostMapping("/upload")
	public String saveImage(@RequestBody Map<String,String> params) {
		//file为文件
		String file = params.get("file");
		log.info("params = " + params);
		String fileName = saveBase64Image(file);
		log.info("保存的图片名称："+fileName);
		return fileName;
	}

	/**
	 * 解析64编码的图片字符串为图片并保存，返回文件的保存路径
	 * @param image
	 * @return
	 */
	public String saveBase64Image(String image) {
		//文件头部信息形式
		//String header = "data:image/jpeg;base64,";
		String header = "data:image/";
		//找不到以字符串 header，说明不是图片
		if (!image.startsWith(header)) {
			return "无图片";
		}
		int pos = image.indexOf(";");
		String imageType = image.substring(header.length(),pos);
		log.info("图片类型："+imageType);
		//去掉头部
		image = image.substring(image.indexOf(",")+1);
		log.info("去除头部："+image);
		BASE64Decoder decoder = new BASE64Decoder();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
		//生成一个文件名
		String fileName = formatter.format(LocalDateTime.now())+"."+imageType;
		//图片保存路径
		String imgFilePath = localPath+ File.separator+fileName;
		try {
			// 对字节数组字符串进行Base64解码并生成图片
			byte[] decodedBytes = decoder.decodeBuffer(image);
			FileOutputStream out = new FileOutputStream(imgFilePath);
			//把图片写入文件
			out.write(decodedBytes);
			out.close();
		} catch (Exception e) {
			log.info("发生异常: "+e);
			//返回json类型
			return null;
			//e.printStackTrace();
		}
		//返回文件路径
		return fileName;
	}

	/**
	 * 解析路径读取图片到前端
	 **/
	@GetMapping("/{fileName}")
	public String getImageBase64(@PathVariable("fileName") String fileName) {
		String msg = null;
		int pos = fileName.lastIndexOf(".")+1;
		String fileType = fileName.substring(pos);
		InputStream in = null;
		byte[] data = null;
		String filePath = localPath+File.separator+fileName;
		// 读取图片字节数组
		try {
			in = new FileInputStream(filePath);
			//in.available()计算字节大小
			data = new byte[in.available()];
			//InputStream把数据读取到字节数组中
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		if (data != null) {
			//返回Base64编码过的字节数组字符串
			String res = "data:image/"+fileType+";Base64,"+encoder.encode(data);
			return "data:image/jpeg;base64," + encoder.encode(data);
		}
		return null;
	}

}
