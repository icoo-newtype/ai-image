package com.aiimage.security;

import com.aiimage.model.AuthInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
  private String secretKey = "____designkit____";

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  // JWT 토큰 생성
  public String createToken(String userId, String userName, String roles) {
    Claims claims = Jwts.claims().setAudience(userId); // JWT payload 에 저장되는 정보단위
    claims.put("name", userName);
    claims.put("roles", roles);
    Date now = new Date();

//    long tokenValidTime = 30 * 60 * 1000L; // 토큰 유효시간 30분
    long tokenValidTime = 24 * 60 * 60 * 1000L; // 토큰 유효시간 24시간

    return Jwts.builder()
            .setClaims(claims) // 정보 저장
            .setIssuedAt(now) // 토큰 발행 시간 정보
            .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
            .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
            .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    String id = claims.getAudience();
    String roles = claims.get("roles", String.class);
    String name = claims.get("name", String.class);
    AuthInfo credentials = new AuthInfo();
    credentials.setId(id);
    credentials.setName(name);
    credentials.setRoles(roles);

    Collection<GrantedAuthority> authorities = new ArrayList<>();
    for (String role : roles.split(",")) {
      authorities.add(new SimpleGrantedAuthority(role));
    }
    return new UsernamePasswordAuthenticationToken(id, credentials, authorities);
  }

  public String resolveToken(HttpServletRequest request) {
    return request.getHeader("X-AUTH-TOKEN");
  }

  public boolean validateToken(String jwtToken) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }
}
