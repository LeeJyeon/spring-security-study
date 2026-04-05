package com.example.security.controller

import com.example.security.service.AuthService
import com.example.security.service.UserService
import io.jsonwebtoken.security.Password
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val authService: AuthService) {

    @PostMapping("/api/login")
    fun login(userName: String, password: String): String =
        authService.authenticate(userName, password)

}