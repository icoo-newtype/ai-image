package com.aiimage.mapper.image;

import com.aiimage.model.image.ImageItem;
import java.util.List;

public interface ImageMapper {
  void insert(ImageItem item);
  List<ImageItem> getList();
}
