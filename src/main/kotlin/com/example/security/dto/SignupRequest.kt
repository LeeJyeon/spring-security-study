package com.example.security.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * ===== Step 3: 회원가입 요청 DTO =====
 *
 * 학습 포인트:
 * - Bean Validation 어노테이션으로 입력값 검증
 * - Entity와 DTO를 분리하여 계층 간 데이터 전달
 */
data class SignupRequest(
    @field:NotBlank(message = "사용자 이름을 입력하세요")
    @field:Size(min = 3, max = 20, message = "사용자 이름은 3~20자여야 합니다")
    val username: String = "",

    @field:NotBlank(message = "비밀번호를 입력하세요")
    @field:Size(min = 4, max = 100, message = "비밀번호는 4자 이상이어야 합니다")
    val password: String = "",

    @field:NotBlank(message = "비밀번호를 다시 입력하세요")
    @field:Size(min = 4, max = 100, message = "비밀번호는 4자 이상이어야 합니다")
    val confirmPassword: String = "",

    @field:NotBlank(message = "이메일을 입력하세요")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String = ""
){
    fun isPasswordConfirmed() {
        if (password != confirmPassword) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다")
        }
    }
}
