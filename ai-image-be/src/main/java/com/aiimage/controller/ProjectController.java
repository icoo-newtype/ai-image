package com.aiimage.controller;

import com.aiimage.model.*;
import com.aiimage.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectController {

  private final ProjectService projectService;

  @GetMapping("/")
  public ResponseEntity<?> index() {
    return ResponseEntity.ok("api home");
  }

  @GetMapping("/project/{slug}")
  public ResponseEntity<?> slugDetail(@ModelAttribute SlugParam param) {
    return ResponseEntity.ok(projectService.slugDetail(param));
  }

  @PostMapping("/project/checkPassword")
  public boolean checkPassword(@ModelAttribute SlugParam param) {
    return projectService.validatePassword(param);
  }

  @GetMapping("/admin/project")
  public ResponseEntity<?> adminList(@ModelAttribute ListParam param) {
    param.setAdmin(true);
    return ResponseEntity.ok(projectService.list(param));
  }

  @GetMapping("/admin/project/{sq}")
  public ResponseEntity<?> adminDetail(@ModelAttribute SqParam param) {
    param.setAdmin(true);
    return ResponseEntity.ok(projectService.detail(param));
  }

  @PostMapping("/admin/project")
  public ResponseEntity<?> put(@ModelAttribute ProjectItem param) throws ParseException {
    return ResponseEntity.ok(projectService.put(param));
  }

  @PostMapping("/admin/project/delete")
  public ResponseEntity<?> delete(@RequestParam("sq") Integer[] sqs) {
    projectService.delete(sqs);
    return ResponseEntity.ok().build();
  }
}
