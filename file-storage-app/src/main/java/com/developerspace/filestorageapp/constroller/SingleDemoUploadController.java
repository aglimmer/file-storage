package com.developerspace.filestorageapp.constroller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 测试使用,一个简单的文件上传
 * @Author wangz
 * @Date 2021-04-17 20:11
 */
@Slf4j
@Api(tags="demo演示文件上传和读取")
@RequestMapping("/app/demo")
@RestController
public class SingleDemoUploadController {

    //本地图片保存的目录，应该在配置文件中指定然后注入，这里是为了演示方便
    private String localPath = "E:\\User\\IDEA\\image";
    //获取图片的URL前缀，这里一般在配置文件中指定然后注入
    private String baseUrl = "http://localhost:8000/app/demo";

    @ApiOperation(value = "上传单个文件")
    @PostMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String,Object> uploadImageFile(@RequestPart("files") MultipartFile multipartFile) {
        return this.uploadImageFileService(multipartFile);
    }
    @ApiOperation(value = "读取图片文件")
    @RequestMapping(value = "/{fileName}", method = RequestMethod.GET, produces = org.springframework.http.MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImageFile(@PathVariable("fileName") String fileName) {
        return getLocalImageFile(localPath,fileName);
    }

    /**
     * service层，解析上传的文件，判断文件大小，指定保存图片的本地目录并上传
     **/
    public Map<String,Object> uploadImageFileService(MultipartFile file) {
        Map<String,Object> result = null;
        try {
            if (file == null) {
                throw new Exception("没有上传有效文件");
            }
            //在限制文件大小的配置类中已限制文件大小，这里可以不用再判断
            //1024*1024=1048574B
            if (file.getSize() >=1048576 ) {
                throw new Exception("文件大小超过了最大限制");
            }
            final String originalFileName = file.getOriginalFilename();
            final InputStream inputStream = file.getInputStream();
            //文件大小单位字节
            final long fileSize = (long)inputStream.available();
            //保存图片的目录
            final String newFileName = this.writeImageFile(this.localPath,originalFileName,inputStream);
            //生成图片的url，要确保通过url能够访问到图片
            final String url =this.baseUrl+"/"+newFileName;
            result = new LinkedHashMap<>();
            result.put("originalName",originalFileName);
            result.put("name",newFileName);
            result.put("size",fileSize);
            result.put("url",url);
            return result;
        } catch (Exception e) {
            log.info("上传失败---error = " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成一个不重复的ID，用作图片名
     */
    public static class UniqueID {
        private static long lastId = 0;
        private static Object locked = new Object();
        public static long getId() {
            long currId = 0;
            //上锁
            synchronized (locked){
                //年、月、日、时、分、秒、毫秒
                currId = Long.parseLong(new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date()).toString()) * 100;
                if (lastId < currId) {
                    lastId = currId;
                } else {
                    lastId = lastId + 1;
                    currId = lastId;
                }
                return currId;
            }
        }
    }

    /**
     * 把图片文件写入磁盘目录
     * @param localBasePath 保存图片的目录，结尾没有"/"
     * @param originalName 源图片名称
     * @param inputStream 输入流
     */
    public String writeImageFile(String localBasePath,String originalName, InputStream inputStream){
        String fileSuffix = originalName.substring(originalName.lastIndexOf("."));
        String newFileName = UniqueID.getId()+fileSuffix;
        File dest = new File(localBasePath + "/" + newFileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            dest.createNewFile();
            FileCopyUtils.copy(inputStream, Files.newOutputStream(dest.toPath()));
        } catch (IOException e) {
            dest.delete();
            log.info("上传失败");
        }
        return newFileName;
    }

    /**
     * 读取本地图片文件为字节
     */
    public byte[] getLocalImageFile(String localBasePath,String fileName) {
        File file = new File(localBasePath + "/" + fileName);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            log.info("获取文件失败");
        }
        return null;
    }
}
