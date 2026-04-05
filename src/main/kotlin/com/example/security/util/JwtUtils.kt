package com.example.security.util

import com.example.security.entity.Role
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.Instant
import java.util.*

class JwtUtils {

    private val secretKey = Keys.hmacShaKeyFor("my-secret-key-must-be-at-least-32-bytes!".toByteArray())
    private val expirationSeconds = 3600L

    fun generateToken(userName: String, role: Role): String = Jwts.builder()
        .subject(userName)
        .claim("role", role.name)
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plusSeconds(expirationSeconds)))
        .signWith(secretKey)
        .compact()

    fun getUserNameFromToken(token: String): String =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload.subject

    fun getRoleFromToken(token: String): Role =
        Role.valueOf(
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload["role"] as String
        )

    fun validateToken(token: String): Boolean = try {
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
        true
    } catch (e: Exception) {
        false
    }
}