package com.frankit.product_manage.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Before http.csrf().disable()"); // 로그 추가
        // 테스트에서 보안 설정을 비활성화
        http.csrf().disable()
                .authorizeRequests()  // Spring Security 5 이전 버전에서 사용됨
                .anyRequest().permitAll();  // 모든 요청을 허용
        System.out.println("After csrf().disable()");
        return http.build();
    }
}
