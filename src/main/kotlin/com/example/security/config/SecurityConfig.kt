package com.example.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * ===== Step 2 & 4: Spring Security 설정 =====
 *
 * 학습 포인트:
 * - @EnableWebSecurity: Spring Security 활성화
 * - SecurityFilterChain: 보안 필터 체인 설정 (Spring Security 6.x 방식)
 * - authorizeHttpRequests: URL별 접근 권한 설정
 * - formLogin: 폼 기반 로그인 설정
 * - 필터 체인을 2개로 분리: API(JWT) / 웹(세션)
 *
 * 핵심 개념: Spring Security는 필터 체인으로 동작합니다.
 *   요청 - [SecurityFilterChain] - 컨트롤러
 *   각 필터가 인증/인가를 순서대로 처리합니다.
 *
 * @Order로 필터 체인 우선순위를 지정합니다.
 *   @Order(1) apiFilterChain: /api 하위 요청을 먼저 매칭
 *   @Order(2) webFilterChain: 나머지 요청을 처리
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val rememberMeServices: RememberMeServices,
    private val authenticationHandler: AuthenticationHandler,
    private val accessDeniedHandler: CustomAccessDeniedHandler
) {

    /**
     * ===== API 전용 필터 체인 (JWT) =====
     *
     * /api 하위 요청만 처리. 세션 없이 JWT 토큰으로 인증.
     * - STATELESS: 서버에 세션을 생성하지 않음
     * - CSRF 비활성화: JWT는 쿠키를 안 쓰므로 CSRF 공격 대상이 아님
     * - JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 배치
     */
    @Bean
    @Order(1)
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // /api/** 경로만 이 필터 체인에서 처리
            .securityMatcher("/api/**")
            // 세션 생성 안 함 (JWT는 Stateless)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            // CSRF 비활성화 (JWT는 쿠키가 아닌 Authorization 헤더 사용)
            .csrf { it.disable() }
            // ===== URL별 접근 권한 =====
            .authorizeHttpRequests { auth ->
                auth
                    // 로그인 API는 누구나 접근 가능 (토큰 발급 받는 곳)
                    .requestMatchers("/api/login").permitAll()
                    // 그 외 API는 인증 필요 (JWT 토큰 필요)
                    .anyRequest().authenticated()
            }
            // JWT 필터를 폼 로그인 필터 앞에 배치
            // 요청: [JwtAuthenticationFilter] - [UsernamePasswordAuthenticationFilter] - ...
            .addFilterBefore(JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    /**
     * ===== 웹 전용 필터 체인 (세션 + 폼 로그인) =====
     *
     * 기존 브라우저 기반 폼 로그인 방식 그대로 유지.
     * /api 이외의 모든 요청을 처리합니다.
     */
    @Bean
    @Order(2)
    fun webFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // ===== Remember Me 설정 =====
            .rememberMe { rememberMe -> rememberMe.rememberMeServices(rememberMeServices) }
            // ===== URL별 접근 권한 설정 =====
            .authorizeHttpRequests { auth ->
                auth
                    // 누구나 접근 가능한 페이지
                    .requestMatchers("/", "/login", "/signup", "/css/**", "/js/**").permitAll()
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
                    .successHandler(authenticationHandler)
                    .failureHandler(authenticationHandler)
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
            .exceptionHandling { ex ->
                ex.accessDeniedHandler(accessDeniedHandler)
            }

        return http.build()
    }
}
