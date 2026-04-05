package com.example.security.service

import com.example.security.entity.Role
import com.example.security.entity.User
import com.example.security.repository.UserRepository
import com.example.security.util.JwtUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class AuthServiceTest {

    @Autowired lateinit var authService: AuthService
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var passwordEncoder: PasswordEncoder

    private val jwtUtils = JwtUtils()

    @Test
    fun `올바른 비밀번호로 로그인하면 JWT 토큰 반환`() {
        val token = authService.authenticate("user", "1234")

        assertTrue(jwtUtils.validateToken(token))
        assertEquals("user", jwtUtils.getUserNameFromToken(token))
        assertEquals(Role.USER, jwtUtils.getRoleFromToken(token))
    }

    @Test
    fun `잘못된 비밀번호로 로그인하면 예외 발생`() {
        assertThrows<IllegalArgumentException> {
            authService.authenticate("user", "wrong")
        }
    }

    @Test
    fun `존재하지 않는 사용자로 로그인하면 예외 발생`() {
        assertThrows<UsernameNotFoundException> {
            authService.authenticate("nobody", "1234")
        }
    }

    @Test
    fun `잘못된 비밀번호 입력 시 실패 횟수 증가`() {
        val before = userRepository.findByUsername("admin")!!.failLoginAttempts

        assertThrows<IllegalArgumentException> {
            authService.authenticate("admin", "wrong")
        }

        val after = userRepository.findByUsername("admin")!!.failLoginAttempts
        assertEquals(before + 1, after)
    }
}
