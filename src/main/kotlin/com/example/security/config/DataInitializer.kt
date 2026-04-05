package com.example.security.config

import com.example.security.entity.Role
import com.example.security.entity.User
import com.example.security.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * ===== 초기 데이터 설정 =====
 *
 * 앱 시작 시 테스트용 계정을 자동 생성합니다.
 * - user / 1234 (일반 사용자)
 * - admin / 1234 (관리자)
 */
@Configuration
class DataInitializer {

    @Bean
    fun initData(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder
    ): CommandLineRunner = CommandLineRunner {
        if (userRepository.count() == 0L) {
            userRepository.saveAll(
                listOf(
                    User(
                        username = "user",
                        password = passwordEncoder.encode("1234"),
                        email = "user@example.com",
                        role = Role.USER
                    ),
                    User(
                        username = "admin",
                        password = passwordEncoder.encode("1234"),
                        email = "admin@example.com",
                        role = Role.ADMIN
                    )
                )
            )
            println("✅ 초기 사용자 데이터 생성 완료!")
            println("   - user / 1234 (일반 사용자)")
            println("   - admin / 1234 (관리자)")
        }
    }
}
