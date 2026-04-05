package com.example.security.repository

import com.example.security.entity.User
import org.springframework.data.jpa.repository.JpaRepository

/**
 * ===== Step 3: 사용자 Repository =====
 *
 * 학습 포인트:
 * - Spring Data JPA가 메서드 이름으로 쿼리를 자동 생성
 * - findByUsername → SELECT * FROM users WHERE username = ?
 */
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}
