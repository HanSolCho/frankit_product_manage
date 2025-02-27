package com.frankit.product_manage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 테스트에서 보안 설정을 비활성화
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll();  // 모든 요청을 허용

        return http.build();
    }
}
