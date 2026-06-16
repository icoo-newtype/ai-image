package com.aiimage.service;

import com.aiimage.exception.ApiException;
import com.aiimage.mapper.ManagerMapper;
import com.aiimage.model.ListParam;
import com.aiimage.model.ListResult;
import com.aiimage.model.LogHistory;
import com.aiimage.model.ManagerItem;
import lombok.RequiredArgsConstructor;
import net.oxizen.OxStr;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ManagerService {

  private final ManagerMapper managerMapper;
  private final LogService logService;

  public ListResult list(ListParam param) {
    List<ManagerItem> list = managerMapper.getList(param);
    Integer count = managerMapper.getCount(param);
    logService.access("관리자 > 모든 관리자", "관리자 목록조회", list.size());
    return new ListResult(list, count, param);
  }

  @Transactional
  public void delete(String[] idArray, ManagerItem param) {
    for (String id : idArray) {
      if (id.equals("master")) throw new ApiException(HttpStatus.UNAUTHORIZED, "master 계정은 말소처리할 수 없습니다.");
      param.setId(id);
      managerMapper.delete(param);
    }
    logService.access("관리자 > 모든 관리자", "말소", idArray.length);
  }

  @Transactional
  public void restore(String[] idArray) {
    for (String id : idArray) {
      managerMapper.restore(id);
    }
    logService.access("관리자 > 모든 관리자", "복원", idArray.length);
  }

  @Transactional
  public void release(String[] idArray) {
    for (String id : idArray) {
      managerMapper.release(id);
    }
    logService.access("관리자 > 모든 관리자", "정지해제", idArray.length);
  }

  @Transactional
  public void register(ManagerItem param) {
    param.setPwd(OxStr.password(param.getPwd()));
    if (managerMapper.checkId(param) > 0) {
      throw new ApiException(HttpStatus.CONFLICT, "동일한 아이디의 계정이 이미 존재합니다.");
    }
    if (param.getRoles().contains("MASTER")) throw new ApiException(HttpStatus.UNAUTHORIZED, "master 권한은 임의로 부여될 수 없습니다.");
    managerMapper.register(param);
    logService.access("관리자 > 모든 관리자", "관리자 등록", 1);
  }

  @Transactional
  public void update(ManagerItem param) {
    if (param.getPwd() != null && !param.getPwd().isEmpty()) param.setPwd(OxStr.password(param.getPwd()));
    if (param.getId().equals("master")) throw new ApiException(HttpStatus.UNAUTHORIZED, "master 계정은 수정할 수 없습니다.");
    if (param.getRoles().contains("MASTER")) throw new ApiException(HttpStatus.UNAUTHORIZED, "master 권한은 임의로 부여될 수 없습니다.");
    if (managerMapper.update(param) > 0) {
      logService.access("관리자 > 모든 관리자", "관리자 정보수정", 1);
    }
  }

  public ListResult loginLog(ListParam param) {
    List<LogHistory> list = managerMapper.loginLogList(param);
    int count = managerMapper.loginLogCount(param);
    return new ListResult(list, count, param);
  }

  public void loginLogXls(ListParam param, Model model) {
    param.setPerPage(0);
    List<?> list = managerMapper.loginLogList(param);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    model.addAttribute("name", "접속로그_" + format.format(new Date()));
    model.addAttribute("list", list);
    model.addAttribute("labels", new String[]{
            "ID", "이름", "접속일시", "접속IP"
    });
    model.addAttribute("keys", new String[]{
            "id", "name", "dtt", "ipAddr"
    });
    logService.access("관리자>접속로그", "엑셀다운로드", list.size());
  }


  public ListResult accessLog(ListParam param) {
    List<LogHistory> list = managerMapper.accessLogList(param);
    int count = managerMapper.accessLogCount(param);
    return new ListResult(list, count, param);
  }

  public void accessLogXls(ListParam param, Model model) {
    param.setPerPage(0);
    List<?> list = managerMapper.accessLogList(param);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    model.addAttribute("name", "운영로그_" + format.format(new Date()));
    model.addAttribute("list", list);
    model.addAttribute("labels", new String[]{
            "ID", "이름", "메뉴", "액션", "조회/처리건", "접속일시", "접속IP"
    });
    model.addAttribute("keys", new String[]{
            "id", "name", "menu", "action", "size", "dtt", "ipAddr"
    });
    logService.access("관리자>운영로그", "엑셀다운로드", list.size());
  }
}
