package com.aiimage.mapper;

import com.aiimage.model.*;

import java.util.List;

public interface ProjectMapper {
  Integer count(ListParam param);

  List<ProjectItem> list(ListParam param);

  ProjectItem detail(SqParam param);

  ProjectItem slugDetail(SlugParam param);

  String getPassword(SlugParam param);

  void insert(ProjectItem param);

  void update(ProjectItem param);

  void delete(SqParam param);

  Integer checkSlug(ProjectItem param);
}