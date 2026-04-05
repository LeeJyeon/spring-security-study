package com.example.security.util

import com.example.security.entity.Role
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JwtUtilsTest {

    private val jwtUtils = JwtUtils()

    @Test
    fun `토큰 생성 후 username 추출`() {
        val token = jwtUtils.generateToken("user", Role.USER)
        assertEquals("user", jwtUtils.getUserNameFromToken(token))
    }

    @Test
    fun `토큰 생성 후 role 추출`() {
        val token = jwtUtils.generateToken("admin", Role.ADMIN)
        assertEquals(Role.ADMIN, jwtUtils.getRoleFromToken(token))
    }

    @Test
    fun `유효한 토큰 검증`() {
        val token = jwtUtils.generateToken("user", Role.USER)
        assertTrue(jwtUtils.validateToken(token))
    }

    @Test
    fun `위조된 토큰은 검증 실패`() {
        assertFalse(jwtUtils.validateToken("invalid.token.value"))
    }

    @Test
    fun `토큰 구조 확인 - 3파트로 구성`() {
        val token = jwtUtils.generateToken("user", Role.USER)
        assertEquals(3, token.split(".").size)
    }
}
