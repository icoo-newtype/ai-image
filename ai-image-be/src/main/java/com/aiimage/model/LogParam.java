package com.aiimage.model;

import lombok.Data;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Data
public class LogParam {
  private String lang;
  private String actor;
  private String menu;
  private String action;
  private Integer size;
  private String ipAddr;
  private String dtt;
  private String id;
  private String roles;

  private LogParam() {
    HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String ip = req.getHeader("X-FORWARDED-FOR");
    if (ip == null)
      this.ipAddr = req.getRemoteAddr();
    else
      this.ipAddr = ip;
  }

  private void generateActor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      this.setActor(authentication.getName());
    }
  }


  /**
   * 로그인 로그 파라미터
   */
  public static LogParam login(LoginParam param) {
    LogParam result = new LogParam();
    result.setActor(param.getId());
    return result;
  }

  /**
   * 메뉴 접근 로그 파라미터
   */
  public static LogParam access(String menu, String action, Integer size) {
    LogParam result = new LogParam();
    result.generateActor();
    result.menu = menu;
    result.action = action;
    result.size = size;
    return result;
  }

  /**
   * 권현 변경 로그 파라미터
   */
//  public static LogParam role(AdminCred cred) {
//    LogParam result = new LogParam();
//    result.generateActor();
//    result.id = cred.getId();
//    result.roles = cred.getRoles();
//    return result;
//  }
}
