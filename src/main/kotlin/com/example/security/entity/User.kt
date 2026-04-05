package com.example.security.entity

import jakarta.persistence.*

/**
 * ===== Step 3: 사용자 엔티티 =====
 *
 * 학습 포인트:
 * - JPA Entity와 Spring Security의 연결
 * - Role(역할)을 Enum으로 관리
 * - 비밀번호는 반드시 암호화하여 저장 (평문 저장 금지!)
 */
@Entity
@Table(name = "users") // 'user'는 H2 예약어이므로 'users' 사용
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val password: String, // BCrypt로 암호화된 비밀번호

    @Column(nullable = false)
    val email: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER,

) {
    @Column(nullable = true)
    var failLoginAttempts: Int = 0
        private set

    fun failLogin() {
        if (failLoginAttempts < 5) {
            failLoginAttempts++
        }
    }

    fun resetLoginAttempts() {
        failLoginAttempts = 0
    }

    fun isLocked(): Boolean = failLoginAttempts >= 5
}
/**
 * 사용자 역할 Enum
 *
 * Spring Security에서는 역할 이름에 "ROLE_" 접두사를 붙여 관리합니다.
 * hasRole("ADMIN") → 내부적으로 "ROLE_ADMIN" 권한을 확인
 */
enum class Role {
    USER,   // 일반 사용자 → ROLE_USER 권한
    ADMIN   // 관리자 → ROLE_ADMIN 권한
}
