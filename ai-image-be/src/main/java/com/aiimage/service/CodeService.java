package com.aiimage.service;

import com.aiimage.mapper.CodeMapper;
import com.aiimage.model.*;
import lombok.RequiredArgsConstructor;
import net.oxizen.spring.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeService {

  private final CodeMapper codeMapper;

  public List<CodeItem> getFullList(CodeItem param) {
    return codeMapper.getFullList(param);
  }

  public List<CodeItem> getList(CodeParam param) {
    return codeMapper.getList(param);
  }

  public CodeItem detail(CodeParam param) {
    return codeMapper.detail(param);
  }

  @Transactional
  public int putNode(CodeItem node) {
    int result;
    if (!node.isEdit() && codeMapper.checkItem(node) > 0) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "이미 사용중인 코드값입니다");
    }
    if (node.isEdit()) {
      result = codeMapper.updateItem(node);
    } else {
      result = codeMapper.insertItem(node);
    }
    return result;
  }

  public int remoteItem(CodeParam param) {
    return codeMapper.removeItem(param);
  }

  public void reorder(List<OrderParam> list) {
    for (OrderParam item : list) {
      codeMapper.setOrder(item);
    }
  }
}
