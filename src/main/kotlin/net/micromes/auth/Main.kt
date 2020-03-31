package net.micromes.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.api.client.util.DateTime
import io.jsonwebtoken.Jwts
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.micromes.auth.db.DBUser
import net.micromes.auth.db.dBConnect
import net.micromes.auth.db.dBInit
import net.micromes.auth.google.GoogleAccount
import net.micromes.auth.google.OAuthClient
import java.security.PrivateKey
import java.time.Instant
import java.util.*


var googleOAuthClient: OAuthClient = OAuthClient()

fun main() {

    //generateKeyPair()
    val key : PrivateKey = loadKeyPair()

    dBConnect()
    dBInit()

    embeddedServer(Netty, 8091) {
        install(ContentNegotiation) {
            jackson {
            }
        }
        routing {
            get("/google") {
                try {
                    val token: String = call.request.headers["Authorization"]?.substring(7)
                        ?: throw RuntimeException("No authentication header")
                    val account: GoogleAccount = googleOAuthClient.authenticate(token)
                    val userID = DBUser().getUserIDByExternalID(account.id).toString()

                    call.respond(createTokenForUserID(key, Payload(sub = userID)))
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.Unauthorized, e.message ?: "ERROR")
                }
            }
            post("/createUser") {
                val token: String = call.request.headers["Authorization"]?.substring(7)
                    ?: throw RuntimeException("No authentication header")
                val account: GoogleAccount = googleOAuthClient.authenticate(token)

                val userName: String = call.request.queryParameters["name"] ?: throw RuntimeException("No name parameter")

                if (DBUser().checkExternalIDExits(account.id)) call.respond(HttpStatusCode.BadRequest, "User does already exist")
                val newId = DBUser().createUserMappingAndReturnID(account.id)

                call.respond(HttpStatusCode.Created, createTokenForUserID(key, Payload(sub = newId.toString(), newUser = true, newName = userName)))
            }
            post("/direct") {
                try {
                    val directID = call.receiveText()
                    println(directID)
                    val userID : String = DBUser().getUserIDByExternalID(directID)?.toString() ?: throw RuntimeException("does not exist")
                    call.respond(createTokenForUserID(key, Payload(sub = userID)))
                } catch (e: RuntimeException) {
                    call.respond(HttpStatusCode.Unauthorized, e.message ?: "ERROR")
                }
            }
        }
    }.start(true)
}

fun createTokenForUserID(key: PrivateKey, payload: Payload) : String {

    return Jwts.builder()
        .setPayload(jacksonObjectMapper().writeValueAsString(payload))
        .signWith(key)
        .compact()
}
