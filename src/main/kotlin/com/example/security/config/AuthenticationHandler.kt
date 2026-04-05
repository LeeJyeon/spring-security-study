package com.example.security.config

import com.example.security.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Service

@Service
class AuthenticationHandler(private val userService: UserService) : AuthenticationSuccessHandler, AuthenticationFailureHandler{


    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        println("성공했니?")
        userService.successLogin(authentication!!.name)
        response!!.sendRedirect("/")
    }

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        val username = request!!.getParameter("username")
        username?.let { userService.failLogin(it) }

        val user = username?.let { userService.findByUsername(it) }
        val redirectUrl = when {
            user != null && user.isLocked() -> "/login?locked"
            else -> "/login?error"
        }
        response!!.sendRedirect(redirectUrl)
    }
}