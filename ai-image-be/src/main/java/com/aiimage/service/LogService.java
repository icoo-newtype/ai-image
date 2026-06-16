package com.aiimage.service;

import com.aiimage.mapper.LogMapper;
import com.aiimage.model.LogParam;
import com.aiimage.model.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class LogService {

  @Autowired
  LogMapper logMapper;

  public void login(LoginParam param) {
    logMapper.login(LogParam.login(param));
  }

  public void access(String menu, String action, Integer size) {
    logMapper.access(LogParam.access(menu, action, size));
  }

  public void access(String menu, String action) {
    access(menu, action, null);
  }
}
