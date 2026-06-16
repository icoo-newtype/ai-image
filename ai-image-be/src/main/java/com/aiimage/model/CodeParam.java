package com.aiimage.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CodeParam extends SqParam {
  private int projSq;
  private String parentCode;
  private String code;
  private String slug;
}
