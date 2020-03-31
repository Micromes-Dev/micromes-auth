package net.micromes.auth

import java.util.*

class Payload(
    val sub: String,
    val iss: String = "micromes-auth",
    val aud: String = "micromes-core",
    val exp: String = Date(Date().time + 1000000).time.toString(),

    val newUser: Boolean = false,
    val newName: String? = null
)