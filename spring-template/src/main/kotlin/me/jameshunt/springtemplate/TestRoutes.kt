package me.jameshunt.springtemplate

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class TestRoutes(private val userRepository: UserRepository) {

    data class BlahInput(val name: String)

    @GetMapping("/blah", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun blah(@RequestBody input: BlahInput): List<User> {
        logger.info("hello")
        return userRepository.getUsers()
    }

    @GetMapping("/error", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun error(@RequestBody input: BlahInput): Nothing {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "wow")
    }
}