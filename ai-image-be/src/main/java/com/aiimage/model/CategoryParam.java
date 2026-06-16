package com.aiimage.model;

import lombok.Data;

@Data
public class CategoryParam {
  private Integer sq;
  private String category;

  public CategoryParam(Integer sq) {
    this.sq = sq;
  }
}
