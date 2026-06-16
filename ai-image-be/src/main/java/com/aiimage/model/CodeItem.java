package com.aiimage.model;

import lombok.Data;

@Data
public class CodeItem {
  private Integer projSq;
  private String parentCode;
  private String code;
  private String label;
  private String data;
  private String access;
  private String article;
  private Integer odr;
  private boolean edit;
}
