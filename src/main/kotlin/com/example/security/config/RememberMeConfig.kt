package com.example.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.RememberMeTokenAlgorithm


@Configuration
class RememberMeConfig {

    companion object {
        val KEY = "my-remember-me-key"
    }

    @Bean
    fun customRememberMe(userDetailsService: UserDetailsService): RememberMeServices {
        val sha256 = RememberMeTokenAlgorithm.SHA256
        val rememberMe = TokenBasedRememberMeServices(KEY, userDetailsService, sha256)
        rememberMe.setMatchingAlgorithm(RememberMeTokenAlgorithm.MD5)
        rememberMe.setAlwaysRemember(false)
        rememberMe.setTokenValiditySeconds(60 * 60 * 24)
        return rememberMe
    }
}