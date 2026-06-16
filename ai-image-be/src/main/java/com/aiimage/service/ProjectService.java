package com.aiimage.service;

import com.aiimage.mapper.CodeMapper;
import com.aiimage.mapper.ProjectMapper;
import com.aiimage.model.*;
import lombok.RequiredArgsConstructor;
import net.oxizen.OxStr;
import net.oxizen.spring.exception.ApiException;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

  private final ProjectMapper projectMapper;
  private final LogService logService;
  private final CodeMapper codeMapper;

  public ListResult list(ListParam param) {
    if (param.getSearch() != null) {
      param.setSearch(OxStr.decodeUri(param.getSearch()));
    }
    List<ProjectItem> list = projectMapper.list(param);
    int count = projectMapper.count(param);
    if (param.isAdmin()) {
      logService.access("프로젝트 리스트", "프로젝트 목록조회", list.size());
    }
    return new ListResult(list, count, param);
  }

  public ProjectItem detail(SqParam param) {

    return projectMapper.detail(param);
  }

  public boolean validatePassword(SlugParam param) {
//    String inputPwd = OxStr.password(param.getPassword());
    String inputPwd = param.getPassword();

    if (inputPwd == null || inputPwd.isEmpty()) {
      return false;
    }

    String storedPwd = projectMapper.getPassword(param);
    return storedPwd != null && storedPwd.equals(inputPwd);
  }

  public ProjectItem slugDetail(SlugParam param) {
    return projectMapper.slugDetail(param);
  }

  @Transactional
  public ProjectItem put(ProjectItem item) throws ParseException {
    if (projectMapper.checkSlug(item) > 0) {
      throw new ApiException(HttpStatus.CONFLICT, "동일한 URL Slug가 존재합니다.");
    }
    if (item.getSq() == null) {
      projectMapper.insert(item);
      logService.access("프로젝트 > 새 프로젝트 등록", "새 프로젝트 등록: " + item.getTitle(), 1);
    } else {
//      item.setPassword(OxStr.password(item.getPassword()));
      projectMapper.update(item);
      logService.access("프로젝트 > 모든 프로젝트", "프로젝트 수정 : " + item.getTitle(), 1);
    }

    return item;
  }

  @Transactional
  public void delete(Integer[] sqs) {
    SqParam param = new SqParam();
    for (Integer sq : sqs) {
      param.setSq(sq);
      projectMapper.delete(param);
      logService.access("프로젝트 > 모든 게시물", "프로젝트 삭제 : " + sq, 1);
    }
  }

}
