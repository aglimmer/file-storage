package com.developerspace.filestorageserver.domain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "file_object")
@Entity
@Getter
@NoArgsConstructor
public class FileObject {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_mode")
    private StorageMode storageMode;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private Long size;

    @Column(name = "url")
    private String url;

    @Column(name = "suffix")
    private String suffix;

    @Column(name = "create_time", updatable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    public void setUrl(String url){
        this.url = url;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }


    public FileObject(StorageMode storageMode, String originalFilename, Long size){
        String fileSuffix = "";
        if(originalFilename.contains(".")){
            fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.getId()+fileSuffix;

        this.storageMode = storageMode;
        this.originalName = originalFilename;
        this.name = fileName;
        this.size = size;
        this.suffix = fileSuffix;
    }

}
