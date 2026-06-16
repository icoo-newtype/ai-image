package com.aiimage.model;

import lombok.Data;

@Data
public class FileItem implements Filekey {
	private String lang;
	private Integer sq;
	private String filekey;
	private String name;
	private String contentType;
	private String mediaType;
	private String fileName;
	private String description;
	private String location;
	private String capacity;

	public FileItem() {}

	public FileItem(String filekey) {
		this.filekey = filekey;
	}

	public FileItem(String filekey, String name, String contentType, String mediaType, String fileName, String location, String capacity, String description) {
		this.filekey = filekey;
		this.name = name;
		this.contentType = contentType;
		this.mediaType = mediaType;
		this.fileName = fileName;
		this.location = location;
		this.capacity = capacity;
		this.description = description;
	}
}
