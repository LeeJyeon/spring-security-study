package com.example.security.config

import com.example.security.util.JwtUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter: OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
       val header = request.getHeader("Authorization")
        if(header != null && header.startsWith("Bearer ")){
            val token = header.substring(7)
            if(JwtUtils().validateToken(token)){
                val username = JwtUtils().getUserNameFromToken(token)
                val role = JwtUtils().getRoleFromToken(token)

                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
                )
            }
        }

        filterChain.doFilter(request, response)
    }
}