package net.micromes.auth

data class User(
    val name: String,
    val id: String? = null,
    val profilePictureURI: String
)