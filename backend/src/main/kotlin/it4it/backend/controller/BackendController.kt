package it4it.backend.controller

import org.springframework.web.bind.annotation.*
import it4it.backend.model.Greeting
import it4it.backend.repository.UserRepository
import it4it.backend.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import java.util.concurrent.atomic.AtomicLong



@RestController
@RequestMapping("/api")
class BackendController() {

    val counter = AtomicLong()

    @Autowired
    lateinit var userRepository: UserRepository

    @GetMapping("/greeting")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String) =
            Greeting(counter.incrementAndGet(), "Hello, $name")

    @GetMapping("/users")
    fun users() = userRepository.findAll()

    @GetMapping("/usercontent")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getUserContent(authentication: Authentication): String {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return "Hello " + user.name + "!"
    }


    @GetMapping("/admincontent")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun getAdminContent(): String {
        return "Admin's content"
    }
}