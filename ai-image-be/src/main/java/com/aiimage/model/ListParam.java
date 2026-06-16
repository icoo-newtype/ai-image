package com.aiimage.model;

import lombok.Data;

@Data
public class ListParam {
  private boolean admin;
  private int pageNo = 1;
  private int perPage = 0;
  private int recentOffset = 0;
  private String searchType;
  private String search;
  private String status;
  private String date;
  private String type;
  private String access;
  private int sq;

  public int getOffset() {
    return (this.getPerPage() == 0 ? 0 : this.perPage * (this.pageNo - 1)) + recentOffset;
  }

  public boolean isPaging() {
    return this.getPerPage() > 0;
  }
}
