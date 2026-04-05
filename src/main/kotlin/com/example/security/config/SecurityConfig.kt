package com.example.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

/**
 * ===== Step 2 & 4: Spring Security 설정 =====
 *
 * 학습 포인트:
 * - @EnableWebSecurity: Spring Security 활성화
 * - SecurityFilterChain: 보안 필터 체인 설정 (Spring Security 6.x 방식)
 * - authorizeHttpRequests: URL별 접근 권한 설정
 * - formLogin: 폼 기반 로그인 설정
 * - PasswordEncoder: 비밀번호 암호화 (BCrypt)
 *
 * ★ 핵심 개념: Spring Security는 필터 체인으로 동작합니다.
 *   요청 → [SecurityFilterChain] → 컨트롤러
 *   각 필터가 인증/인가를 순서대로 처리합니다.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // ===== URL별 접근 권한 설정 =====
            .authorizeHttpRequests { auth ->
                auth
                    // 누구나 접근 가능한 페이지
                    .requestMatchers("/", "/signup", "/css/**", "/js/**").permitAll()
                    // H2 콘솔 접근 허용 (개발용)
                    .requestMatchers("/h2-console/**").permitAll()
                    // /admin/** 경로는 ADMIN 역할만 접근 가능
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    // 그 외 모든 요청은 인증 필요
                    .anyRequest().authenticated()
            }
            // ===== 폼 로그인 설정 =====
            .formLogin { form ->
                form
                    .loginPage("/login")              // 커스텀 로그인 페이지 경로
                    .loginProcessingUrl("/login")      // 로그인 폼 action URL
                    .defaultSuccessUrl("/", true)      // 로그인 성공 시 이동할 페이지
                    .failureUrl("/login?error")        // 로그인 실패 시 이동할 페이지
                    .permitAll()                       // 로그인 페이지는 누구나 접근 가능
            }
            // ===== 로그아웃 설정 =====
            .logout { logout ->
                logout
                    .logoutUrl("/logout")              // 로그아웃 URL
                    .logoutSuccessUrl("/login?logout") // 로그아웃 성공 시 이동할 페이지
                    .invalidateHttpSession(true)       // 세션 무효화
                    .deleteCookies("JSESSIONID")       // 쿠키 삭제
            }
            // ===== H2 콘솔을 위한 설정 (개발용) =====
            .csrf { csrf ->
                csrf.ignoringRequestMatchers("/h2-console/**")
            }
            .headers { headers ->
                headers.frameOptions { it.sameOrigin() }
            }

        return http.build()
    }

    /**
     * PasswordEncoder Bean 등록
     *
     * BCrypt: 단방향 해시 함수 + Salt 자동 생성
     * - 같은 비밀번호도 매번 다른 해시값 생성
     * - Rainbow Table 공격 방어
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
