package com.developerspace.filestorageserver.domain.entity;

/**
 * @Author wangz
 * @Date 2021-04-06 0:07
 */
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StorageMode {

    ALIYUN("阿里云OSS"), LOCAL("本地OSS");

    private String label;


    public String getLabel() {
        return label;
    }
}
