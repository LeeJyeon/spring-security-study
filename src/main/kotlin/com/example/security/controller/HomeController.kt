package com.example.security.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal

/**
 * ===== Step 1: 기본 페이지 컨트롤러 =====
 *
 * 학습 포인트:
 * - Spring MVC의 기본 컨트롤러 구조
 * - Principal 객체로 현재 로그인한 사용자 정보 접근
 * - Thymeleaf 템플릿과의 연동
 */
@Controller
class HomeController {

    @GetMapping("/")
    fun home(model: Model, principal: Principal?): String {
        model.addAttribute("username", principal?.name ?: "Guest")
        return "home"
    }

    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }

    @GetMapping("/mypage")
    fun myPage(model: Model, principal: Principal): String {
        model.addAttribute("username", principal.name)
        return "mypage"
    }

    @GetMapping("/admin")
    fun adminPage(model: Model, principal: Principal): String {
        model.addAttribute("username", principal.name)
        return "admin"
    }

    @GetMapping("/access-denied")
    fun accessDenied(): String {
        return "access-denied"
    }
}
