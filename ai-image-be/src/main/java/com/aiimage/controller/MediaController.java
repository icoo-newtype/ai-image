package com.aiimage.controller;

import com.aiimage.model.FileParam;
import com.aiimage.model.SqParam;
import com.aiimage.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api")
public class MediaController {

  final private MediaService mediaService;

  @RequestMapping(value = "/media/images", method = RequestMethod.GET)
  public ResponseEntity<?> fullList(@ModelAttribute SqParam param) {
    return ResponseEntity.ok(mediaService.getImageAll(param));
  }

  @RequestMapping(value = "/media/description", method = RequestMethod.GET)
  public ResponseEntity<?> getDescription(@ModelAttribute FileParam param) {
    return ResponseEntity.ok(mediaService.getDescription(param));
  }

  @RequestMapping(value = "/admin/media/preSignedUrl", method = RequestMethod.GET)
  public ResponseEntity<?> getPreSignedUrl(@ModelAttribute FileParam param) {
    return ResponseEntity.ok(mediaService.getPreSignedUrl(param));
  }

  @RequestMapping(value = "/admin/media/add/{filekey}", method = RequestMethod.POST)
  public ResponseEntity<?> add(@ModelAttribute FileParam param, MultipartHttpServletRequest multipart) throws IOException {
    mediaService.upload(multipart, param);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/admin/media/description/{filekey}/{sq}", method = RequestMethod.POST)
  public ResponseEntity<?> description(@ModelAttribute FileParam param) {
    mediaService.description(param);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/admin/media/delete/{filekey}/{sq}", method = RequestMethod.DELETE)
  public ResponseEntity<?> del(@ModelAttribute FileParam param) throws IOException {
    mediaService.delete(param);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/admin/media/list/{filekey}/{mediaType}", method = RequestMethod.GET)
  public ResponseEntity<?> list(@ModelAttribute FileParam param) {
    return ResponseEntity.ok(mediaService.getFiles(param));
  }
}