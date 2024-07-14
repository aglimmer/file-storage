package com.developerspace.filestorageserver.controller;


import com.developerspace.filestoragesdk.Result;
import com.developerspace.filestoragesdk.api.ForwardStorageImageApi;
import com.developerspace.filestoragesdk.response.FileUploadResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
public class ForwardStorageImageController implements ForwardStorageImageApi {

    @Value(value = "${forwardFile.baseUrl}")
    public String serverBaseUrl;

    @Resource
	RestTemplate restTemplate;

    public Result<FileUploadResponse> uploadImageForward(MultipartFile multipartFile) throws IOException {
        MultiValueMap request = new LinkedMultiValueMap(1);
        ByteArrayResource byteArrayResource = new ByteArrayResource(multipartFile.getBytes()) {
            @Override // 需要覆盖默认的方法，不然无法获取名称
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        // 此处从multipartFile获取byte[],如果是上传本地文件可以使用io获取byte[]
        request.add("fileName", byteArrayResource);
        String uploadUrl = this.serverBaseUrl+"/upload";
        FileUploadResponse response = restTemplate.postForObject(uploadUrl, request, FileUploadResponse.class);
        return new Result<>(response);
    }

    public byte[] getImage(String fileName) {
        String imgUrl = this.serverBaseUrl+"/"+fileName;
        //log.info("url = " + imgUrl);
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header("Content-Disposition", "attachment; filename=\"" + 113232 + "\"")
//                .body(new byte[]{1});
        ResponseEntity<byte[]> ans =  restTemplate.getForEntity(imgUrl,byte[].class);
        return ans.getBody();
    }
    public ResponseEntity<byte[]> getImage2(String fileName) {
        String imgUrl = this.serverBaseUrl+"/"+fileName;
        //log.info("url = " + imgUrl);
//        return ResponseEntity.ok()
//                .contentLength()
//                .contentType(MediaType.APPLICATION_ATOM_XML)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header("Content-Disposition", "attachment; filename=\"" + 113232 + "\"")
//                .body(new byte[]{1});
        byte []bts = new byte[1024];
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.ms-excel");
        headers.add("Connection", "close");
        headers.add("Accept-Ranges", "bytes");
//        String ss  ="";
//        ss.contains()
        return new ResponseEntity<byte[]>(bts,headers, HttpStatus.OK);
//        ResponseEntity<byte[]> ans =  restTemplate.getForEntity(imgUrl,byte[].class);
//        return ans.getBody();
    }

}
