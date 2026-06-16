package com.aiimage.controller;

import com.aiimage.model.ListParam;
import com.aiimage.model.ManagerItem;
import com.aiimage.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/manager")
public class ManagerController {

  private final ManagerService managerService;

  @Operation(summary = "관리자 목록 조회")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "관리자 목록"),
  })
  @GetMapping("/list")
  public ResponseEntity<?> list(@ModelAttribute ListParam param) {
    return ResponseEntity.ok(managerService.list(param));
  }

  @Operation(summary = "관리자 말소")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "관리자 말소"),
  })
  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  @GetMapping("/delete")
  public ResponseEntity<?> delete(@RequestParam("id") String[] idArray, @ModelAttribute ManagerItem param) {
    managerService.delete(idArray, param);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "관리자 복원")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "관리자 복원"),
  })
  @PostMapping("/restore")
  public ResponseEntity<?> restore(@RequestParam("id") String[] idArray) {
    managerService.restore(idArray);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "관리자 정지해제")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "관리자 정지해제"),
  })
  @PostMapping("/release")
  public ResponseEntity<?> release(@RequestParam("id") String[] idArray) {
    managerService.release(idArray);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "관리자 등록")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "관리자 등록"),
  })
  @PostMapping("/register")
  public ResponseEntity<?> register(@ModelAttribute ManagerItem managerItem) {
    managerService.register(managerItem);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "관리자 수정")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "관리자 수정"),
  })
  @PostMapping("/update")
  public ResponseEntity<?> update(@ModelAttribute ManagerItem managerItem) {
    managerService.update(managerItem);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/loginLog", method = RequestMethod.GET)
  public ResponseEntity<?> loginLog(@ModelAttribute ListParam param) {
    return ResponseEntity.ok(managerService.loginLog(param));
  }

  @RequestMapping(value = "/loginLog/xls", method = RequestMethod.GET)
  public String loginLogXls(@ModelAttribute ListParam param, Model model) {
    managerService.loginLogXls(param, model);
    return "excelView";
  }

  @RequestMapping(value = "/accessLog", method = RequestMethod.GET)
  public ResponseEntity<?> accessLog(@ModelAttribute ListParam param) {
    return ResponseEntity.ok(managerService.accessLog(param));
  }

  @RequestMapping(value = "/accessLog/xls", method = RequestMethod.GET)
  public String accessLogXls(@ModelAttribute ListParam param, Model model) {
    managerService.accessLogXls(param, model);
    return "excelView";
  }
}
