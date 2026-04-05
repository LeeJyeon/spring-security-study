package com.example.security.controller

import com.example.security.service.AuthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class AuthController(private val authService: AuthService) {

    // JWT 토큰 발급
    @PostMapping("/api/login")
    fun login(@RequestBody request: LoginRequest): Map<String, String> {
        val token = authService.authenticate(request.username, request.password)
        return mapOf("token" to token)
    }

    // 내 정보 조회 (JWT 인증 필요)
    @GetMapping("/api/me")
    fun me(principal: Principal): Map<String, String> {
        return mapOf("username" to principal.name)
    }

    // 인증 확인용 (JWT 인증 필요)
    @GetMapping("/api/hello")
    fun hello(principal: Principal): Map<String, String> {
        return mapOf("message" to "안녕하세요, ${principal.name}님!")
    }

    // ADMIN 전용 API (JWT + ADMIN 역할 필요)
    @GetMapping("/api/admin/stats")
    fun adminStats(principal: Principal): Map<String, Any> {
        return mapOf(
            "admin" to principal.name,
            "totalUsers" to 2,
            "message" to "관리자 전용 데이터입니다"
        )
    }
}

data class LoginRequest(
    val username: String,
    val password: String
)