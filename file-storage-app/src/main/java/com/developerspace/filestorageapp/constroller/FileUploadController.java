package com.developerspace.filestorageapp.constroller;

import com.developerspace.filestoragesdk.Result;
import com.developerspace.filestoragesdk.api.LocalStorageImageApi;
import com.developerspace.filestoragesdk.response.FileUploadResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author wangz
 * @Date 2022-01-08 10:27
 */
@Slf4j
@Api(tags="file-storage-app测试")
@RestController
@RequestMapping("/app")
public class FileUploadController {
	@Resource
	LocalStorageImageApi localStorageImageApi;

	@Autowired
	HttpServletRequest request;

	@ApiOperation("测试")
	@GetMapping
	public String getMessage(){
		return "请求成功";
	}

	 // 图片链接访问方式
	 @ApiOperation(value = "读取图片")
	 @RequestMapping(value = "/image/{fileName}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	 public byte[] getImageByte(@PathVariable("fileName") String fileName){
		 String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
		 boolean state = suffix.matches("\\.jpg|\\.gif|\\.png|\\.bmp|\\.jpeg||\\.gif||\\.ico");
		 if(!state){
			 throw new RuntimeException("非法的图片格式:"+fileName);
		 }
	 	return localStorageImageApi.getFileResponse(fileName).getBody();
	 }

	 @ApiOperation(value = "获取文件")
	 @RequestMapping(value = "/{fileName}", method = RequestMethod.GET)
	 public ResponseEntity<byte[]> getFileResponse(@PathVariable("fileName") String fileName){
	 	return localStorageImageApi.getFileResponse(fileName);
	 }
	@ApiOperation(value = "带参数上传文件")
	@PostMapping(value="/upload",headers = {"content-type=multipart/form-data"},consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Result<FileUploadResponse> uploadFile(@RequestPart("files") MultipartFile multipartFile){
		log.info("request.header.AccessToken=" + request.getHeader("AccessToken"));
		return localStorageImageApi.uploadFile(multipartFile);
	}

	@ApiOperation(value = "带参数上传文件")
	@PostMapping(value="/upload/params",headers = {"content-type=multipart/form-data"},consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Result<FileUploadResponse> uploadFileParams(@RequestPart("files") MultipartFile multipartFile, @RequestParam("params") String params){
		log.info("file-app----upload params = " + params);
		log.info("request.header.AccessToken=" + request.getHeader("AccessToken"));
		return localStorageImageApi.uploadFileWithParams(multipartFile,params);
	}

}
