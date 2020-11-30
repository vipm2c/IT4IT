package it4it.backend.controller

import org.springframework.web.bind.annotation.*
import it4it.backend.model.Greeting
import it4it.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
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
}