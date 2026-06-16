package com.aiimage.controller;

import com.aiimage.exception.ApiException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Controller
public class ExceptionController implements ErrorController {

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.setDisallowedFields("");
  }

  @ExceptionHandler({ApiException.class})
  public ResponseEntity<?> apiException(ApiException e) {
    return e.getEntity();
  }

  @ExceptionHandler({MissingServletRequestParameterException.class})
  public ResponseEntity<?> missingParameterException() {
    return new ApiException(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", "필수 파라미터가 누락되었습니다.").getEntity();
  }

  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  public ResponseEntity<?> requestMethodNotSupportedException() {
    return new ApiException(HttpStatus.BAD_REQUEST, "REQUEST_METHOD_NOT_SUPPORTED", "지원되지 않는 메소드입니다.").getEntity();
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<?> commonException(Exception e) {
    e.printStackTrace();
    String massage = e.getMessage().isEmpty() ? "내부 서버 오류입니다." : e.getMessage();
    return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", massage).getEntity();
  }

  @RequestMapping(value = ERROR_PATH)
  public ResponseEntity<?> error(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    int code = status == null ? 0 : Integer.parseInt(status.toString());
    ApiException exception;
    switch (code) {
      case 403:
        exception = new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "권한이 없습니다.");
        break;
      case 404:
        exception = new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않습니다.");
        break;
      default:
        exception = new ApiException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다.");
        break;
    }
    return exception.getEntity();
  }

  private static final String ERROR_PATH = "/error";
}
