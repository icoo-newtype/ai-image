package com.aiimage.mapper.image;

import com.aiimage.model.image.ImageItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImageMapper {
  void insert(ImageItem item);
  List<ImageItem> getList();
}
