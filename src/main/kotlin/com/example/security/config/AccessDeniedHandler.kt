package com.example.security.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Service

@Service
class AccessDeniedHandler: AccessDeniedHandler{
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?
    ) {
        val username = request!!.getParameter("username")
        println("username [$username] has no role")

        redirection(response)
//        foward(response, request)
    }

    private fun redirection(response: HttpServletResponse?) {
        response!!.sendRedirect("/access-denied")
    }

    private fun foward(
        response: HttpServletResponse?,
        request: HttpServletRequest
    ) {
        response!!.status = HttpServletResponse.SC_FORBIDDEN  // 403 유지
        request!!.getRequestDispatcher("/access-denied").forward(request, response)
    }
}