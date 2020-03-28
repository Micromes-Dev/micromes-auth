package net.micromes.auth

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Payload(
    val sub: String,
    val iss: String = "micromes-auth",
    val aud: String = "micromes-core",
    val exp: String = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE),

    val name: String,
    val profilePictureURL: String
) {
    constructor(user: User) : this(
        sub = user.id.toString(),
        name = user.name,
        profilePictureURL = user.profilePictureURI
    )
}