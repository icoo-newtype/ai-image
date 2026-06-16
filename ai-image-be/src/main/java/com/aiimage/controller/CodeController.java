package com.aiimage.controller;

import com.aiimage.model.CodeItem;
import com.aiimage.model.CodeParam;
import com.aiimage.model.OrderParam;
import com.aiimage.model.SqParam;
import com.aiimage.service.CodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CodeController {

  private final CodeService service;

  @RequestMapping(value = "/code/{projSq}", method = RequestMethod.GET)
  public ResponseEntity<?> fullList(@ModelAttribute CodeItem param) {
    return ResponseEntity.ok(service.getFullList(param));
  }

  @Operation(summary = "카테고리 목록 조회")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "카테고리 목록 조회"),
  })
  @GetMapping(value = "/code/list/{parentCode}")
  public ResponseEntity<?> list(@ModelAttribute CodeParam param) {
    return ResponseEntity.ok(service.getList(param));
  }

  @Operation(summary = "어드민 카테고리 목록 조회")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "어드민 카테고리 목록 조회"),
  })
  @GetMapping(value = "/admin/code/list/{projSq}/{parentCode}")
  public ResponseEntity<?> adminList(@ModelAttribute CodeParam param) {
    param.setAdmin(true);
    return ResponseEntity.ok(service.getList(param));
  }

  @GetMapping("/admin/code/{slug}/{code}")
  public ResponseEntity<?> adminDetail(@ModelAttribute CodeParam param) {
    param.setAdmin(true);
    return ResponseEntity.ok(service.detail(param));
  }

  @Operation(summary = "카테고리 항목 추가 및 수정")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "카테고리 항목 추가 및 수정"),
  })
  @PostMapping(value = "/admin/code")
  public ResponseEntity<?> put(@ModelAttribute CodeItem codeItem) {
    return ResponseEntity.ok(service.putNode(codeItem));
  }

  @Operation(summary = "카테고리 항목 순서 변경")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "카테고리 항목 순서 변경"),
  })
  @PostMapping(value = "/admin/code/order/{projSq}")
  public ResponseEntity<?> reorder(@RequestBody List<OrderParam> list) {
    service.reorder(list);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "카테고리 항목 삭제")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "카테고리 항목 삭제"),
  })
  @DeleteMapping(value = "/admin/code")
  public ResponseEntity<?> delete(@ModelAttribute CodeParam param) {
    return ResponseEntity.ok(service.remoteItem(param));
  }

}
