package com.developerspace.filestoragesdk.api;

import com.developerspace.filestoragesdk.Result;
import com.developerspace.filestoragesdk.response.FileUploadResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author wangz
 * @Date 2022-01-07 16:49
 */
// 在没有使用功能注册中心的情况下，需要指定确切的url地址
@Api(value = "图片上传")
@FeignClient(name = "file-storage-server", url = "localhost:7999")
@RequestMapping("/file")
public interface LocalStorageImageApi {

	@ApiOperation(value = "上传文件")
	@PostMapping(value="/upload/params",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	Result<FileUploadResponse> uploadFileWithParams(@RequestPart("files") MultipartFile multipartFile, @RequestParam("params") String params);


	/**
	 * 单文件上传
	 * Feign接口上需要正确使用注解指定MediaType，否则会出现以下错误
	 * feign.codec.EncodeException: Content-Type "multipart/form-data" not set for request body of type StandardMultipartFile
	 * @param multipartFile
	 * @return
	 */
	@ApiOperation(value = "上传文件")
	@PostMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	Result<FileUploadResponse> uploadFile(@RequestPart("files") MultipartFile multipartFile);

	@PostMapping(value="/upload/list")
	Result<List<FileUploadResponse>> uploadFileList(@RequestParam("files") MultipartFile[] files);


	@ApiOperation(value = "获取文件")
	@RequestMapping(value = "/{fileName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	ResponseEntity<byte[]> getFileResponse(@PathVariable("fileName") String fileName);
	//produces = MediaType.TEXT_PLAIN_VALUE

	@ApiOperation(value = "下载文件")
	@RequestMapping(value = "/download/{fileName}", method = RequestMethod.GET)
	ResponseEntity<byte[]> getDownloadFileResponse(@PathVariable("fileName") String fileName);

	@ApiOperation(value = "链接下载")
	@RequestMapping(value = "/outlink/download", method = RequestMethod.GET)
	ResponseEntity<byte[]> getDownloadOutlinkResponse(@RequestParam("url") String url);


	@ApiOperation(value = "链接下载")
	@RequestMapping(value = "/outlink/download/resource", method = RequestMethod.GET)
	ResponseEntity<InputStreamResource> getInputStream(@RequestParam("url") String url);
}
