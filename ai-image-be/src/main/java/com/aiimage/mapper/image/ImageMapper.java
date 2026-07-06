package com.aiimage.mapper.image;

import com.aiimage.model.image.ImageItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ImageMapper {
  void insert(ImageItem item);
  List<ImageItem> getList(@Param("lastSq") long lastSq, @Param("size") int size);
}
