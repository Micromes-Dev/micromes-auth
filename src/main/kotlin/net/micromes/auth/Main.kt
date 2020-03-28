package net.micromes.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.micromes.auth.db.DBUser
import net.micromes.auth.google.GoogleAccount
import net.micromes.auth.google.OAuthClient
import java.security.Key


var googleOAuthClient: OAuthClient = OAuthClient()
var key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

fun main() {
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
                    val httpClient = HttpClient()
                    val userID = DBUser().getUserIDByExternalID(account.id).toString()
                    val userRaw: String = httpClient.get("http://localhost:8090/userByID?id=${userID}")
                    val user: User = jacksonObjectMapper().readValue(userRaw)

                    val claims: HashMap<String, Any?> = HashMap()
                    claims["user_name"] = user.name
                    claims["user_id"] = user.id
                    claims["user_profile_picture_url"] = user.profilePictureURI

                    val jwt: String = Jwts.builder()
                        .setClaims(claims)
                        .signWith(key)
                        .compact()

                    call.respond(jwt)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.Unauthorized, e.message ?: "ERROR")
                }

            }
        }
    }.start(true)
}