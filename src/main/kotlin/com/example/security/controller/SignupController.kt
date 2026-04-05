package com.example.security.controller

import com.example.security.dto.SignupRequest
import com.example.security.service.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

/**
 * ===== Step 3: 회원가입 컨트롤러 =====
 *
 * 학습 포인트:
 * - 폼 데이터 바인딩 (@ModelAttribute)
 * - Bean Validation (@Valid)
 * - 서비스 레이어를 통한 사용자 등록
 */
@Controller
class SignupController(
    private val userService: UserService
) {

    @GetMapping("/signup")
    fun signupForm(model: Model): String {
        model.addAttribute("signupRequest", SignupRequest())
        return "signup"
    }

    @PostMapping("/signup")
    fun signup(
        @Valid @ModelAttribute signupRequest: SignupRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            return "signup"
        }

        return try {
            userService.createUser(signupRequest)
            "redirect:/login?registered"
        } catch (e: IllegalArgumentException) {
            model.addAttribute("error", e.message)
            "signup"
        }
    }
}
