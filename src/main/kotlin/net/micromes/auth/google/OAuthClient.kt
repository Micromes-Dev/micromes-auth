package net.micromes.auth.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import net.micromes.auth.google.GoogleAccount
import net.micromes.auth.config.Settings
import java.io.IOException
import java.lang.RuntimeException
import java.security.GeneralSecurityException
import java.util.*

class OAuthClient{
    var transport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    var jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
    private val clientId = Settings.CLIENT_ID
    val clientSecret = Settings.CLIENT_SECRET
    private var verifier: GoogleIdTokenVerifier

    init{
        verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(Collections.singletonList((clientId)))
            .build()
    }

    fun authenticate(idTokenString: String): GoogleAccount {
        //println("Start verifying")
        var idToken: GoogleIdToken? = null
        try {
            idToken = verifier.verify(idTokenString)
        }catch(e: GeneralSecurityException){
            println(e)
        }catch (e: IOException){
            println(e)
        }

        if (idToken != null) {
            //println("Not null")
            val payload: GoogleIdToken.Payload = idToken.payload
            // Print user identifier
            val userId: String = payload.subject
            println("User ID: $userId")

            // Get profile information from payload
            val email: String = payload.email
            val emailVerified: Boolean = payload.emailVerified
            val name = payload["name"] as String
            val pictureUrl = payload["picture"] as String
            val locale = payload["locale"] as String
            val givenName = payload["given_name"] as String

            return GoogleAccount(
                userId, email, emailVerified,
                name, pictureUrl, locale, givenName
            )
        } else {
            println("Invalid ID token.")
            throw RuntimeException("Invalid token")
        }
    }
}