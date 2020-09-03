package me.jameshunt.springtemplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class SpringTemplateApplication

fun main(args: Array<String>) {
    runApplication<SpringTemplateApplication>(*args)
}

typealias UserId = UUID

@Throws(IllegalArgumentException::class)
fun String.toUUID(): UUID = UUID.fromString(this).also {
    if (it.toString() != this) throw IllegalArgumentException("Invalid UUID")
}