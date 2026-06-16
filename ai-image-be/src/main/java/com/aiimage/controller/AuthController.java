package com.aiimage.controller;

import com.aiimage.service.AuthService;
import com.aiimage.model.AuthInfo;
import com.aiimage.model.ChangePwd;
import com.aiimage.model.LoginParam;
import com.aiimage.service.GrammarCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

  private final AuthService authService;
  private final GrammarCheckService grammarCheckService;

  @Operation(summary = "인증된 계정 정보 확인")
  @GetMapping("/auth/info")
  public AuthInfo info(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
    return authentication != null ? (AuthInfo) authentication.getCredentials() : null;
  }

  @Operation(summary = "관리자 로그인")
  @PostMapping("/auth/login")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "로그인 성공"),
          @ApiResponse(responseCode = "401", description = "아이디 혹은 비밀번호가 일치하지 않음"),
  })
  public String login(LoginParam loginParam) {
    return authService.login(loginParam);
  }

  @PostMapping(value = "/admin/grammarCheck")
  public Map<String, Object> check(@RequestParam("text") String text) {
    return grammarCheckService.checkGrammar(text);
  }
}
