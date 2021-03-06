package it4it.backend.service

import it4it.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Service
class UserDetailsServiceImpl: UserDetailsService{

    @Autowired
    lateinit var userRepository: UserRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(p0: String?): UserDetails {
        val user = p0?.let { userRepository.findByUsername(it).get() }
                ?: throw UsernameNotFoundException("User'$p0' not found")

        val userRole = SimpleGrantedAuthority("ROLE_USER")
        val userRoles = mutableListOf(userRole)
        if (user.admin){
            val adminRole = SimpleGrantedAuthority("ROLE_ADMIN")
            userRoles.add(adminRole)
        }
        val authorities: List<GrantedAuthority> = userRoles.toList<GrantedAuthority>()

        return org.springframework.security.core.userdetails.User
                .withUsername(p0)
                .password(user.getCredential())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.active)
                .build()
    }

}
