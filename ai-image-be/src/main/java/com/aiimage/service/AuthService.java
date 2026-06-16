package com.aiimage.service;

import com.aiimage.exception.ApiException;
import com.aiimage.mapper.ManagerMapper;
import com.aiimage.model.LoginParam;
import com.aiimage.model.ManagerItem;
import com.aiimage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final ManagerMapper managerMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final LogService logService;
  private final RestTemplate restTemplate;

  public String login(LoginParam loginParam) {
    // newtype.design에서 아이디/비밀번호 검증
    ManagerItem manager = verifyFromNT(loginParam);

    if (!manager.getId().equals("master")) { // master 계정은 만료되지 않아야함
      if (manager.getStatus() > 1) {
        throw new net.oxizen.spring.exception.ApiException(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 일치하지 않습니다.");
      }
      if (manager.getWrongCount() > 4) {
        throw new net.oxizen.spring.exception.ApiException(HttpStatus.UNAUTHORIZED, "5회이상 비번 오류로 계정이 정지된 상태입니다");
      }
      if (manager.getLoginAge() != null && manager.getLoginAge() > 2) {
        throw new net.oxizen.spring.exception.ApiException(HttpStatus.UNAUTHORIZED, "장기 미접속으로 계정이 정지된 상태입니다");
      }
    }
    managerMapper.loginSuccess(loginParam);
    logService.login(loginParam);
    return jwtTokenProvider.createToken(manager.getId(), manager.getName(), manager.getRoles());
  }

  private ManagerItem verifyFromNT(LoginParam loginParam) {
    try {
      ResponseEntity<ManagerItem> response = restTemplate.postForEntity(
              "https://newtype.design/api/auth/internal/verify",
              loginParam,
              ManagerItem.class
      );
      if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
        throw new ApiException(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 일치하지 않습니다.");
      }
      return response.getBody();
    } catch (HttpClientErrorException e) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 일치하지 않습니다.");
    }
  }
}
