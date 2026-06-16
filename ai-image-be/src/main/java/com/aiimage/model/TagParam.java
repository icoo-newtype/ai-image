package com.aiimage.model;

import lombok.Data;

@Data
public class TagParam {
  private Integer sq;
  private String tag;
  private Integer odr;

  public TagParam(Integer sq) {
    this.sq = sq;
  }
}
