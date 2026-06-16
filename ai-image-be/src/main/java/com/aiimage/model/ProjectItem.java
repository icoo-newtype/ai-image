package com.aiimage.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectItem extends SqParam {
  private String slug;
  private String title;
  private String type;
  private String client;
  private String access;
  private String password;
  private String logoImage;
  private String footerLogoImage;
  private String ogImage;
  private String resource;
  private int hasPassword;

  private String registerDtt;
  private String updateDtt;
}