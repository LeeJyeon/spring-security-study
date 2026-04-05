package com.example.security.service

import com.example.security.dto.SignupRequest
import com.example.security.entity.Role
import com.example.security.entity.User
import com.example.security.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * ===== Step 3: 사용자 서비스 =====
 *
 * 학습 포인트:
 * - 비밀번호를 반드시 PasswordEncoder로 암호화하여 저장
 * - 사용자 이름 중복 검사
 * - @Transactional로 트랜잭션 관리
 */
@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createUser(request: SignupRequest): User {
        // 사용자 이름 중복 확인
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("이미 존재하는 사용자 이름입니다: ${request.username}")
        }

        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password), // ★ 비밀번호 암호화!
            email = request.email,
            role = Role.USER
        )

        return userRepository.save(user)
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
}
