package com.developerspace.filestorageserver.domain.entity;

import lombok.Data;

/**
 * @Author wangz
 * @Date 2022-03-08 17:38
 */
@Data
public class CustomParams {
	private String fileName;
	private Boolean overwrite;
}
