package com.developerspace.filestoragesdk.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author wangz
 * @Date 2022-01-07 16:50
 */
@Data
public class FileUploadResponse {

	private Long id;

	private String originalName;

	private String name;

	private Long size;

	private String url;

	private String suffix;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime updateTime;
}
