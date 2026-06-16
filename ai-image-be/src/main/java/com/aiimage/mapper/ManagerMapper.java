package com.aiimage.mapper;

import com.aiimage.model.*;

import java.util.List;

public interface ManagerMapper {
  ManagerItem getManager(LoginParam param);

  List<ManagerItem> getList(ListParam param);

  Integer getCount(ListParam param);

  Integer checkId(ManagerItem param);

  void delete(ManagerItem id);

  void restore(String id);

  void release(String id);

  void register(ManagerItem param);

  Integer update(ManagerItem param);

  void changePassword(ChangePwd param);

  void loginFail(LoginParam param);

  void loginSuccess(LoginParam param);

  List<LogHistory> loginLogList(ListParam param);

  Integer loginLogCount(ListParam param);

  List<LogHistory> accessLogList(ListParam param);

  Integer accessLogCount(ListParam param);
}