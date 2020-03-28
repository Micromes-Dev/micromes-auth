package net.micromes.auth

import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Payload(
    val sub: String,
    val iss: String = "micromes-auth",
    val aud: String = "micromes-core",
    val exp: String = Date(Date().time + 1000000).time.toString(),

    val name: String,
    val profilePictureURL: String
) {
    constructor(user: User) : this(
        sub = user.id.toString(),
        name = user.name,
        profilePictureURL = user.profilePictureURI
    )
}