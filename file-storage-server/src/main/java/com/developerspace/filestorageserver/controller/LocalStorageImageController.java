package com.developerspace.filestorageserver.controller;

import com.developerspace.filestoragesdk.Result;
import com.developerspace.filestoragesdk.api.LocalStorageImageApi;
import com.developerspace.filestoragesdk.response.FileUploadResponse;
import com.developerspace.filestorageserver.domain.entity.FileObject;
import com.developerspace.filestorageserver.domain.service.LocalFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class LocalStorageImageController implements LocalStorageImageApi {

    private final LocalFileService localFileService;

    private final HttpServletRequest request;

    @Override
    public Result<FileUploadResponse> uploadFileWithParams(MultipartFile multipartFile, String params) {
        log.info("file-server----multipartFile = " + multipartFile + ", params = " + params);
        log.info("multipartFile = " + multipartFile + ", params = " + params);
        log.info("request.header.AccessToken=" + request.getHeader("AccessToken"));
        final FileObject fileObject = localFileService.uploadLocalFile(multipartFile);
        if(fileObject == null){
            return null;
        }
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        BeanUtils.copyProperties(fileObject,fileUploadResponse);
        return new Result<>(fileUploadResponse);
    }
    public Result<FileUploadResponse> uploadFile(MultipartFile multipartFile) {
        final FileObject fileObject = localFileService.uploadLocalFile(multipartFile);
        if(fileObject == null){
            return null;
        }
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        BeanUtils.copyProperties(fileObject,fileUploadResponse);
        return new Result<>(fileUploadResponse);
    }

    /**
     * 多文件上传
     *
     * @param files
     * @return
     */
    public Result<List<FileUploadResponse>> uploadFileList(MultipartFile[] files){
        List<FileUploadResponse> fileObjects = new ArrayList<>();
        for(MultipartFile multipartFile:files){
            FileObject fileObject = localFileService.uploadLocalFile(multipartFile);
            FileUploadResponse fileUploadResponse = new FileUploadResponse();
            BeanUtils.copyProperties(fileObject,fileUploadResponse);
            fileObjects.add(fileUploadResponse);
        }
        return new Result<>(fileObjects);
    }
    public ResponseEntity<byte[]> getFileResponse(String fileName) {
        return localFileService.getLocalFileResponse(fileName);
    }
    public ResponseEntity<byte[]> getDownloadFileResponse(String fileName){
        return localFileService.getDownloadFileResponse(fileName);
    }
    public ResponseEntity<byte[]> getDownloadOutlinkResponse(String url){
        return localFileService.downloadOutLink(url);
    }

    @Override
    public ResponseEntity<InputStreamResource> getInputStream(String url){
        return localFileService.readInputStreamResponseByUrl(url);
    }

}

