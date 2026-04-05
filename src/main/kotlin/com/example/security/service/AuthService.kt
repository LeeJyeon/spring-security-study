package com.example.security.service

import com.example.security.repository.UserRepository
import com.example.security.util.JwtUtils
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun authenticate(username: String, password: String): String {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다: $username")

        if(user.isLocked()){
            throw IllegalStateException("비밀번호 5회 이상 실패로, 계정이 잠겼습니다: $username")
        }

        if(passwordEncoder.matches(password, user.password)){

            return JwtUtils().generateToken(user.username, user.role)
        }
        user.failLogin()
        userRepository.save(user)
        throw IllegalArgumentException("잘못된 비밀번호입니다: $username")
    }



}