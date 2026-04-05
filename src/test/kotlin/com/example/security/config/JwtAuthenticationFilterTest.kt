package com.example.security.config

import com.example.security.entity.Role
import com.example.security.util.JwtUtils
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockFilterChain
import org.springframework.security.core.context.SecurityContextHolder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class JwtAuthenticationFilterTest {

    private val jwtUtils = JwtUtils()
    private val filter = JwtAuthenticationFilter()

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `유효한 토큰이면 SecurityContext에 인증 정보 세팅`() {
        val token = jwtUtils.generateToken("user", Role.USER)
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer $token")

        filter.doFilter(request, MockHttpServletResponse(), MockFilterChain())

        val auth = SecurityContextHolder.getContext().authentication
        assertNotNull(auth)
        assertEquals("user", auth.name)
    }

    @Test
    fun `Authorization 헤더가 없으면 인증 안 됨`() {
        val request = MockHttpServletRequest()

        filter.doFilter(request, MockHttpServletResponse(), MockFilterChain())

        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `잘못된 토큰이면 인증 안 됨`() {
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer invalid.token.value")

        filter.doFilter(request, MockHttpServletResponse(), MockFilterChain())

        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `Bearer 접두사 없으면 인증 안 됨`() {
        val token = jwtUtils.generateToken("user", Role.USER)
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", token)

        filter.doFilter(request, MockHttpServletResponse(), MockFilterChain())

        assertNull(SecurityContextHolder.getContext().authentication)
    }
}
