package com.developerspace.filestoragesdk.api;

import com.developerspace.filestoragesdk.Result;
import com.developerspace.filestoragesdk.response.FileUploadResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author wangz
 * @Date 2022-01-07 17:59
 */
/**
 * @FeignClient注解： name指定微服务服务名称，url指定地址
 * @Author wangz
 * @Date 2021-09-12 16:24
 */
@Api(value = "图片上传——测试")
@FeignClient(name = "file-storage-server", url = "localhost:7999")
@RequestMapping("/image/cloud")
public interface ForwardStorageImageApi {

	@ApiOperation(value = "上传文件")
	@PostMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Result<FileUploadResponse> uploadImageForward(@RequestPart("file") MultipartFile multipartFile) throws IOException;

	@ApiOperation(value = "获取文件")
	@RequestMapping(value = "/{fileName}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImage(@PathVariable("fileName") String fileName);
}
