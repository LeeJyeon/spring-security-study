package com.example.security.service

import com.example.security.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * ===== Step 3: 커스텀 UserDetailsService =====
 *
 * ★ Spring Security의 핵심 인터페이스!
 *
 * 학습 포인트:
 * - Spring Security는 인증 시 UserDetailsService.loadUserByUsername()을 호출
 * - DB에서 사용자를 조회하여 UserDetails 객체로 반환
 * - UserDetails에는 사용자 이름, 암호화된 비밀번호, 권한 목록이 포함
 *
 * 인증 흐름:
 * 1. 사용자가 로그인 폼에서 username/password 입력
 * 2. Spring Security가 loadUserByUsername(username) 호출
 * 3. DB에서 사용자 조회 → UserDetails 반환
 * 4. Spring Security가 입력된 password와 UserDetails의 password를 비교 (BCrypt)
 * 5. 일치하면 인증 성공 → SecurityContext에 Authentication 저장
 */
@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다: $username")

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(SimpleGrantedAuthority("ROLE_${user.role.name}"))
            .build()
    }
}
