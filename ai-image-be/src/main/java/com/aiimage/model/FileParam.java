package com.aiimage.model;

import lombok.Data;

@Data
public class FileParam implements Filekey {
	private Integer sq;
	private String lang;
	private String filekey;
	private String name;
	private String mediaType;
	private String description;
}
