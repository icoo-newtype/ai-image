package com.aiimage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GrammarCheckService {

  @Value("${gemini.api.key}")
  private String apiKey;

  private final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

  public Map<String, Object> checkGrammar(String inputText) {
    RestTemplate restTemplate = new RestTemplate();
    String url = API_URL + apiKey;

    // 1. 프롬프트 구성 (시스템 역할 부여)
    String systemPrompt = "너는 한국어 교정 전문가야. 입력된 텍스트의 맞춤법과 띄어쓰기를 검사해서 '반드시' 다음 JSON 형식으로만 응답해. 그리고 단순한 오타 교정뿐만 아니라, 문맥상 어색한 표현이나 마케팅적으로 더 세련된 문장 제안도 포함해서 JSON으로 만들어줘."
            + "{ \"corrections\": [ {\"original\": \"원문\", \"replacement\": \"수정안\", \"reason\": \"이유\"} ] }";

    // 2. 요청 바디 구성 (Gemini API 구조에 맞춤)
    Map<String, Object> requestBody = new HashMap<>();
    List<Map<String, Object>> contents = new ArrayList<>();
    Map<String, Object> content = new HashMap<>();
    List<Map<String, String>> parts = new ArrayList<>();
    Map<String, String> part = new HashMap<>();

    part.put("text", systemPrompt + "\n\n텍스트: " + inputText);
    parts.add(part);
    content.put("parts", parts);
    contents.add(content);
    requestBody.put("contents", contents);

    // 3. 헤더 설정 및 API 호출
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    try {
      ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
      return response.getBody();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
