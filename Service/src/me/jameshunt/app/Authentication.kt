package me.jameshunt.app

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

class JWTAuth(password: String) {
    private val algorithm = Algorithm.HMAC256(password)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}