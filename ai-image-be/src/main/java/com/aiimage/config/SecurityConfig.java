package com.aiimage.config;

import com.aiimage.security.JwtAuthenticationFilter;
import com.aiimage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtTokenProvider jwtTokenProvider;

  @Value("${allow-origin}")
  private String[] allow_origin;

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/project/**").permitAll()
            .antMatchers("/api/image/**").permitAll()
            .antMatchers("/api/admin/**").hasAnyAuthority("MASTER", "ADMIN", "MANAGER", "MEMBER")
            .antMatchers("/api/admin/manager/**").hasAnyAuthority("MASTER", "ADMIN")
            .antMatchers("/**").permitAll()
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .cors();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedMethod("*");
    for (String origin : allow_origin) {
      configuration.addAllowedOrigin(origin);
    }
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
  }
}