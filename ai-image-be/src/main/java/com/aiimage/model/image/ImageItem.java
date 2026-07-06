package com.aiimage.model.image;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImageItem {
  private Long sq;
  private String prompt;
  private String model;
  private String s3Key;
  private String url;
  private LocalDateTime regDtt;
  private String actor;
}
