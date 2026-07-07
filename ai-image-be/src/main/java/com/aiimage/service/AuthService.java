package com.aiimage.service;

import com.aiimage.exception.ApiException;
import com.aiimage.model.LoginParam;
import com.aiimage.model.NtUserInfo;
import com.aiimage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RestTemplate restTemplate;

  public String login(LoginParam loginParam) {
    NtUserInfo user = verifyFromNT(loginParam);
    return jwtTokenProvider.createToken(user.getId(), user.getName(), user.getRoles());
  }

  private NtUserInfo verifyFromNT(LoginParam loginParam) {
    try {
      ResponseEntity<NtUserInfo> response = restTemplate.postForEntity(
              "https://newtype.design/api/auth/internal/verify",
              loginParam,
              NtUserInfo.class
      );
      if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
        throw new ApiException(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 일치하지 않습니다.");
      }
      return response.getBody();
    } catch (RestClientException e) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 일치하지 않습니다.");
    }
  }
}
