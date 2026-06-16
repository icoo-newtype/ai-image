package com.aiimage.model;

import lombok.Data;

@Data
public class LogHistory {
	private String lang;
	private String id;
	private String name;
	private String menu;
	private String action;
	private Integer size;
	private String dtt;
	private String ipAddr;
}
