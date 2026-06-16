package com.aiimage.model;

import lombok.Data;

@Data
public class RelatedItem {
  private Integer sq;
  private Integer relatedSq;
  private Integer odr;

  public RelatedItem(Integer sq) {
    this.sq = sq;
  }
}
