package it4it.backend.controller

import it4it.backend.jwt.JwtProvider
import it4it.backend.model.LoginUser
import it4it.backend.user.NewUser
import it4it.backend.repository.UserRepository
import it4it.backend.user.User
import it4it.backend.user.role.RoleService
import it4it.backend.web.response.JwtResponse
import it4it.backend.web.response.ResponseMessage
import it4it.backend.web.response.SuccessfulSigninResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
class AuthController() {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    @Autowired
    lateinit var jwtProvider: JwtProvider

    @Autowired
    lateinit var roleService: RoleService

    @Value("\${ksvg.app.authCookieName}")
    lateinit var authCookieName: String

    @Value("\${ksvg.app.isCookieSecure}")
    var isCookieSecure: Boolean = true

    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginUser, response: HttpServletResponse): ResponseEntity<*> {

        val userCandidate: Optional<User> = userRepository.findByUsername(loginRequest.username!!)

        if (userCandidate.isPresent) {
            val user: User = userCandidate.get()
            val authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password))
            SecurityContextHolder.getContext().setAuthentication(authentication)
            val jwt: String = jwtProvider.generateJwtToken(user.username!!)

            val cookie: Cookie = Cookie(authCookieName, jwt)
            cookie.maxAge = jwtProvider.jwtExpiration!!
            cookie.secure = isCookieSecure
            cookie.isHttpOnly = true
            cookie.path = "/"
            response.addCookie(cookie)

            val projectRoles = roleService.getUserManagerProjects(user)
            val userRole = SimpleGrantedAuthority("ROLE_USER")
            val userRoles = mutableListOf(userRole)
            if (user.admin){
                val adminRole = SimpleGrantedAuthority("ROLE_ADMIN")
                userRoles.add(adminRole)
            }
            projectRoles.forEach{
                userRoles.plus(SimpleGrantedAuthority("MANAGER_"+it.project!!.key!!.toUpperCase()))
            }
            val authorities: List<GrantedAuthority> = userRoles.toList<GrantedAuthority>()
            return ResponseEntity.ok(JwtResponse(jwt, user.username, authorities))
        } else {
            return ResponseEntity(ResponseMessage("User not found!"),
                    HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody newUser: NewUser): ResponseEntity<*> {

        val userCandidate: Optional <User> = userRepository.findByUsername(newUser.username!!)

        if (!userCandidate.isPresent) {
            if (usernameExists(newUser.username!!)) {
                return ResponseEntity(ResponseMessage("Username is already taken!"),
                        HttpStatus.BAD_REQUEST)
            } else if (emailExists(newUser.email!!)) {
                return ResponseEntity(ResponseMessage("Email is already in use!"),
                        HttpStatus.BAD_REQUEST)
            }

            // Creating user's account
            val user = User(
                    0,
                    newUser.name!!,
                    newUser.username!!,
                    newUser.email!!,
                    encoder.encode(newUser.credential),
                    active = true,
                    admin = false
            )

            userRepository.save(user)

            return ResponseEntity(ResponseMessage("User registered successfully!"), HttpStatus.OK)
        } else {
            return ResponseEntity(ResponseMessage("User already exists!"),
                    HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<*> {
        val cookie: Cookie = Cookie(authCookieName, null)
        cookie.maxAge = 0
        cookie.secure = isCookieSecure
        cookie.isHttpOnly = true
        cookie.path = "/"
        response.addCookie(cookie)

        return ResponseEntity.ok(ResponseMessage("Successfully logged"))
    }

    private fun emailExists(email: String): Boolean {
        return userRepository.findByUsername(email).isPresent
    }

    private fun usernameExists(username: String): Boolean {
        return userRepository.findByUsername(username).isPresent
    }


}