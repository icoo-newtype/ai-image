package com.aiimage.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import com.aiimage.mapper.FileMapper;
import com.aiimage.model.FileItem;
import com.aiimage.model.FileParam;
import com.aiimage.model.SqParam;
import lombok.RequiredArgsConstructor;
import net.oxizen.spring.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MediaService {

  final private FileMapper fileMapper;
  final private AmazonS3 amazonS3;

  @Value("${ncblog.s3bucket}")
  private String bucket;

  public Map<String, String> getPreSignedUrl(FileParam param){
    Date expiration = new Date();
    long expTimeMillis = expiration.getTime();
    expTimeMillis += 1000 * 60 * 60; // 1시간
    expiration.setTime(expTimeMillis);

    String ext = param.getName() != null ? param.getName().substring(param.getName().lastIndexOf(".") + 1) : "";
    String key = param.getFilekey() + "/" + UUID.randomUUID().toString() + "." + ext;
    GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, key)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration);

    generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL,
            CannedAccessControlList.PublicRead.toString());

    URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

    Map<String, String> result = new HashMap<>();
    result.put("url", url.toExternalForm());
    result.put("key", key);

    return result;
  }

  private String makeKey() {
    Calendar today = Calendar.getInstance();
    int year = today.get(Calendar.YEAR);
    int month = today.get(Calendar.MONTH) + 1;
    long key = today.getTimeInMillis();
    return year + "_" + month + "_" + key;
  }

  public void upload(MultipartHttpServletRequest request, FileParam param) throws IOException {
    String filekey = param.getFilekey() != null ? param.getFilekey() : request.getParameter("filekey");
    if (filekey != null) {
      String[] removeFiles = request.getParameterValues("removeFiles");
      if (removeFiles != null) {
        for (String sq : removeFiles) {
          param.setSq(Integer.parseInt(sq));
          fileMapper.deleteFile(param);
        }
      }
    } else {
      filekey = makeKey();
      param.setFilekey(filekey);
    }

    Iterator<String> names = request.getFileNames();
    while (names.hasNext()) {
      String name = names.next();
      List<MultipartFile> files = request.getFiles(name);
      for (MultipartFile file : files) {
        if (file.isEmpty()) continue;
        String fileName = file.getOriginalFilename();
        String ext = fileName != null ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
        String contentType = file.getContentType();
        String mediaType = param.getMediaType();
        String saveId = UUID.randomUUID().toString();
        String location = filekey + "/" +  saveId + "." + ext;
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(file.getSize());
        meta.setContentType(file.getContentType());

        amazonS3.putObject(new PutObjectRequest(bucket, location, file.getInputStream(), meta).withCannedAcl(CannedAccessControlList.PublicRead));
        fileMapper.insertFile(new FileItem(filekey, name, contentType, mediaType, fileName, location, String.valueOf(file.getSize()), param.getDescription()));
      }
    }
  }

  @Cacheable(cacheNames = "imageDescription", key="{#param.sq}", unless = "#result == null")
  public String getDescription(FileParam param) {
    return fileMapper.getDescription(param);
  }

  public void description(FileParam param) {
    fileMapper.description(param);
  }

  public void delete(FileParam param) throws IOException {
    FileItem fileItem = fileMapper.getFile(param);
    if (fileItem == null) throw new ApiException("항목이 없습니다.");
    amazonS3.deleteObjects(new DeleteObjectsRequest(bucket).withKeys(fileItem.getLocation()));
    fileMapper.deleteFile(param);
  }

  public List<FileItem> getFiles(FileParam param) {
    return fileMapper.getFiles(param);
  }

  @Cacheable(cacheNames = "images")
  public List<FileItem> getImageAll(SqParam param) {
    return fileMapper.getImageAll(param);
  }
}