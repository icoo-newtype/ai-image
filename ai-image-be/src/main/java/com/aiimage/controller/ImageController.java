package com.aiimage.controller;

import com.aiimage.model.image.ImageItem;
import com.aiimage.service.ImageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/admin/image/save")
  public ResponseEntity<ImageItem> save(@RequestBody SaveRequest req, Authentication authentication) {
    ImageItem item = imageService.save(req.getB64Image(), req.getPrompt(), req.getModel(), authentication);
    return ResponseEntity.ok(item);
  }

  @GetMapping("/image/list")
  public ResponseEntity<List<ImageItem>> list(
      @RequestParam(defaultValue = "0") long lastSq,
      @RequestParam(defaultValue = "15") int size) {
    return ResponseEntity.ok(imageService.getList(lastSq, size));
  }

  @Data
  static class SaveRequest {
    private String b64Image;
    private String prompt;
    private String model;
  }
}
