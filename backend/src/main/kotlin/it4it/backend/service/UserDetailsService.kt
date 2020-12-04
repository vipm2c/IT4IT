package it4it.backend.service

import it4it.backend.user.User
import org.springframework.security.core.userdetails.UserDetailsService

interface UserDetailsService : UserDetailsService {

    fun createVerificationTokenForUser(token: String, user: User)

    fun validateVerificationToken(token: String): String
}