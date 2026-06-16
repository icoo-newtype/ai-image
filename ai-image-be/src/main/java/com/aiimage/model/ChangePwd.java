package com.aiimage.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChangePwd extends LoginParam {
  private String newPwd;
}
