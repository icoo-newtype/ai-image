package com.aiimage.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthInfo implements Serializable {
  private String id;
  private String name;
  private String roles;
}
