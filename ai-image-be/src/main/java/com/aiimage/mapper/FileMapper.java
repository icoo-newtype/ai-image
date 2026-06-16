package com.aiimage.mapper;

import com.aiimage.model.FileItem;
import com.aiimage.model.FileParam;
import com.aiimage.model.SqParam;

import java.util.List;

public interface FileMapper {

  void insertFile(FileItem param);

  void deleteFile(FileParam param);

  void description(FileParam param);

  String getDescription(FileParam param);

  FileItem getFile(FileParam param);

  List<FileItem> getFiles(FileParam filekey);

  List<FileItem> getImageAll(SqParam param);

}
