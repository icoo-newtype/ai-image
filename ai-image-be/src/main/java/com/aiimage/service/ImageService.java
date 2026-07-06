package com.aiimage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.aiimage.mapper.image.ImageMapper;
import com.aiimage.model.image.ImageItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageMapper imageMapper;
  private final AmazonS3 amazonS3;

  @Value("${aiimage.s3bucket}")
  private String bucket;

  public ImageItem save(String b64Image, String prompt, String model, Authentication authentication) {
    byte[] imageBytes = Base64.getDecoder().decode(b64Image);

    LocalDate today = LocalDate.now();
    String s3Key = today.getYear() + "/" + String.format("%02d", today.getMonthValue()) + "/" + UUID.randomUUID() + ".png";

    ObjectMetadata meta = new ObjectMetadata();
    meta.setContentLength(imageBytes.length);
    meta.setContentType("image/png");

    amazonS3.putObject(new PutObjectRequest(bucket, s3Key, new ByteArrayInputStream(imageBytes), meta));

    String url = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + s3Key;

    String actor = authentication != null ? authentication.getName() : null;

    ImageItem item = new ImageItem();
    item.setPrompt(prompt);
    item.setModel(model);
    item.setS3Key(s3Key);
    item.setUrl(url);
    item.setActor(actor);
    imageMapper.insert(item);

    return item;
  }

  public List<ImageItem> getList() {
    return imageMapper.getList();
  }
}
