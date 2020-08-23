package me.jameshunt.app

import java.util.*
import kotlin.jvm.Throws

@Throws(IllegalArgumentException::class)
fun String.toUUID(): UUID = UUID.fromString(this).also {
    if (it.toString() != this) throw IllegalArgumentException("Invalid UUID")
}