package com.aiimage.mapper;

import com.aiimage.model.LogParam;

public interface LogMapper {
  void login(LogParam param);

  void access(LogParam param);
}
