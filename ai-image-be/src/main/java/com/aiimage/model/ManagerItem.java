package com.aiimage.model;

import lombok.Data;

@Data
public class ManagerItem {
  private String id;
  private String name;
  private String pwd;
  private String regDtt;
  private String roles;
  private String lastLogin;
  private Integer status;
  private Integer wrongCount;
  private String pwdChangeDtt;
  private String quitDtt;
  private String quitReason;
  private Integer pwdAge;
  private Integer loginAge;
}
