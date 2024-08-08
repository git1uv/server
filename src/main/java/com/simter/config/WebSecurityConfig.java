package com.simter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll() // Swagger 관련 경로 접근 허용
                        .requestMatchers("/api/v1/airplane").permitAll()
                        .requestMatchers("/api/v1/airplane/*").permitAll()
                        .requestMatchers("/api/v1/*").permitAll()
                        .requestMatchers("/*").permitAll()
                        .requestMatchers("/api/v1/mail/*").permitAll()
                        .requestMatchers("/api/v1/mail/stared/*").permitAll()

                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
