package it4it.backend.jwt

import it4it.backend.service.UserDetailsServiceImpl
import java.io.IOException

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter


class JwtAuthTokenFilter : OncePerRequestFilter() {

    @Autowired
    private val tokenProvider: JwtProvider? = null

    @Autowired
    private val userDetailsService: UserDetailsServiceImpl? = null

    @Value("\${ksvg.app.authCookieName}")
    lateinit var authCookieName: String

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {

            val jwt = getJwt(request)
            if (jwt != null && tokenProvider!!.validateJwtToken(jwt)) {
                val username = tokenProvider.getUserNameFromJwtToken(jwt)

                val userDetails = userDetailsService!!.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities())
                authentication.setDetails(WebAuthenticationDetailsSource().buildDetails(request))

                SecurityContextHolder.getContext().setAuthentication(authentication)
            }
        } catch (e: Exception) {
            logger.error("Can NOT set user authentication -> Message: {}", e)
        }

        filterChain.doFilter(request, response)
    }

    private fun getJwt(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "")
        }
        else {
            for (cookie in request.cookies) {
                if (cookie.name == authCookieName) {
                    return cookie.value
                }
            }
            return null
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JwtAuthTokenFilter::class.java)
    }
}