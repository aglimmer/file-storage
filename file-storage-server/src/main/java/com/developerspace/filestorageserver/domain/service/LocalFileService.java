package com.developerspace.filestorageserver.domain.service;

import com.developerspace.filestorageserver.config.LocalFileProperties;
import com.developerspace.filestorageserver.domain.entity.StorageMode;
import com.developerspace.filestorageserver.domain.entity.FileObject;
import com.developerspace.filestorageserver.domain.repository.FileObjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author wangz
 * @Date 2021-04-05 23:53
 */
@Slf4j
@Service
@AllArgsConstructor
public class LocalFileService {
    private final FileObjectRepository fileObjectRepository;
    private final LocalFileProperties localFileProperties;

    public Map<String,Object> saveMultipartFile(MultipartFile multipartFile) throws IOException {
        Map<String, Object> results = new HashMap<>();
        if (multipartFile.getSize() >= 1024 * 1024 * 10) {
            throw new RuntimeException("文件大小超过了最大限制10M");
        }
        final String oriName = multipartFile.getOriginalFilename();
        // 原始文件名不能保证唯一性，需要生成一个唯一文件名，此处仅简单示例，实际开发中文件名称的生成规则需要确保唯一性
        String newName = System.currentTimeMillis() + oriName.substring(oriName.lastIndexOf(".")); //
        String fullPath = System.getProperty("user.dir") + "/" + newName;
        final InputStream inputStream = multipartFile.getInputStream();
        File file = new File(fullPath);
        // 也可以直接使用 multipartFile.transferTo(file);
        if (file.createNewFile()) {
            FileCopyUtils.copy(inputStream, Files.newOutputStream(file.toPath()));
        }
        results.put("url","http://127.0.0.1:7999/file/"+newName);
        return results;
    }



    public FileObject uploadLocalFile(MultipartFile file) {
        FileObject fileObject = null;
        try {
            if (file == null) {
                throw new Exception("没有上传有效文件");
            }
            if (file.getSize() >= localFileProperties.getMaxSize()) {
                throw new Exception("文件大小超过了最大限制");
            }
            final String originalFilename = file.getOriginalFilename();
            final InputStream inputStream = file.getInputStream();
            fileObject = new FileObject(StorageMode.LOCAL, originalFilename, (long) inputStream.available());
            if (this.writeFile(fileObject.getName(), inputStream)) {
                fileObject.setUrl(localFileProperties.getBaseUrl() + "/" + fileObject.getName());
                fileObjectRepository.save(fileObject);
                return fileObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败");
        }
        return fileObject;
    }


    public boolean writeFile(String filename, InputStream inputStream) {
        File dest = new File(localFileProperties.getPath() + "/" + filename);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            dest.createNewFile();
            FileCopyUtils.copy(inputStream, Files.newOutputStream(dest.toPath()));
        } catch (IOException e) {
            dest.delete();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ResponseEntity<byte[]> getLocalFileResponse(String fileName) {
        byte[] bytes = getLocalFile(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, getFileMediaType(fileName).toString());
        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }

    public static byte[] convert(InputStreamResource inputStreamResource) throws IOException {
        try (InputStream inputStream = inputStreamResource.getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return bytes;
        }
    }

    // 根据文件名后缀确定图片的 MIME 类型
    public MediaType getFileMediaType(String imageName) {
        String fileExtension = imageName.substring(imageName.lastIndexOf(".") + 1).toLowerCase();
        switch (fileExtension) {
            case "jpeg":
            case "jpg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "bmp":
                return MediaType.parseMediaType("image/bmp");
            default:
                return MediaType.APPLICATION_OCTET_STREAM; // 默认为二进制流
        }
    }


    public byte[] getLocalFile(String fileName) {
        File file = new File(localFileProperties.getPath() + "/" + fileName);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("获取文件失败:",e);
            throw new RuntimeException("找不到指定的文件");
        }
    }

    public InputStreamResource readFileAsResource(String fullPath) {
        File file = new File(fullPath);
        if (!file.exists()) {
            throw new RuntimeException("文件找不到");
        }
        FileInputStream inputStream = null;
        InputStreamResource inputStreamResource = null;
        try {
            inputStream = new FileInputStream(file);
            inputStreamResource = new InputStreamResource(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("读取文件失败：" + fullPath);
        }
        return inputStreamResource;
    }

    public InputStream getInputStream(byte[] bytes) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
        InputStream inputStream = null;
        try {
            inputStream = byteArrayResource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputStream;

    }

    public ResponseEntity<byte[]> downloadOutLink(String fileUrl) {
        log.info("downloadOutLink fileUrl：{}", fileUrl);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(fileUrl, HttpMethod.GET, null, byte[].class);
        byte[] bytes = responseEntity.getBody();
        log.info("return entity...");
        HttpHeaders oldHeaders = responseEntity.getHeaders();
        oldHeaders.entrySet().forEach(entry -> {
            log.info("headName:{}, headValue:{}", entry.getKey(), entry.getValue());
        });
        String contentDisposition = oldHeaders.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        String contentType = oldHeaders.getFirst(HttpHeaders.CONTENT_TYPE);
        HttpHeaders responseHeaders = new HttpHeaders();
        if (contentDisposition != null && !Objects.equals("", contentDisposition)) {
            responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        } else {
            // 获取一个文件扩展名
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
            String fileName = System.currentTimeMillis()+"."+fileSuffix;
            responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s",fileName));
        }
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, contentType);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(bytes);
    }

    /**
     * 请求外部链接地址，并返回输入流
     * java.io.IOException: stream is closed
     * @param url
     * @return
     */
    public ResponseEntity<InputStreamResource> readInputStreamResponseByUrl_test(String url) {
        log.info("readInputStreamResponse---url:{}",url);
        RestTemplate restTemplate = new RestTemplate();
        // 使用 ResponseExtractor 来处理响应
        ResponseEntity<InputStreamResource> responseEntity = restTemplate.execute(
                url,
                HttpMethod.GET,
                null,
                clientHttpResponse -> {
                    HttpHeaders headers = clientHttpResponse.getHeaders();
                    InputStream inputStream = clientHttpResponse.getBody();
                    InputStreamResource resource = new InputStreamResource(inputStream);
                    return new ResponseEntity<>(resource, headers, clientHttpResponse.getStatusCode());
                });
        return responseEntity;
        // org.springframework.web.client.RestClientException: Error while extracting response for type [class org.springframework.core.io.InputStreamResource]
        // and content type [image/png]; nested exception is org.springframework.http.converter.HttpMessageNotReadableException: Unsupported resource class: class org.springframework.core.io.InputStreamResource
        //错误用法： ResponseEntity<InputStreamResource> responseEntity = restTemplate.exchange(fileUrl,HttpMethod.GET,null,InputStreamResource.class);
    }

    public ResponseEntity<InputStreamResource> readInputStreamResponseByUrl(String url) {
        log.info("readInputStreamResponse---url:{}",url);
        RestTemplate restTemplate = new RestTemplate();
        // 使用 ResponseExtractor 来处理响应
        ResponseEntity<InputStreamResource> responseEntity = restTemplate.execute(
                url,
                HttpMethod.GET,
                null,
                clientHttpResponse -> {
                    HttpHeaders headers = clientHttpResponse.getHeaders();
                    // 将 InputStream 读取到 ByteArrayOutputStream 中，以确保不会被关闭
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    InputStream inputStream = clientHttpResponse.getBody();
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    // 将 ByteArrayOutputStream 转换为 ByteArrayInputStream
                    InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
                    return new ResponseEntity<>(resource, headers, clientHttpResponse.getStatusCode());
                });
        return responseEntity;
       }

    public ResponseEntity<byte[]> getDownloadFileResponse(String fileName) {
        byte[] imageBytes = this.getLocalFile(fileName);
        String contentType = getFileMediaType(fileName).toString();
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Custom-Action", "download");
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add("Access-Control-Expose-Headers", "Content-Type,Content-Disposition,Custom-Action");
        // InputStreamResource inputStreamResource = this.readResource(fullPath);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

}
