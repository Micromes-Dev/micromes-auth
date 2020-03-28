package net.micromes.auth.config

import java.net.URI
import java.time.format.DateTimeFormatter

class Settings {
    companion object {
        val DEFAULT_LOGO_URL: URI = URI.create("http://mtorials.de/logo.png/")
        const val CLIENT_ID = "1025113353398-pb40di8kma99osibf68j8ov8fqvddr96.apps.googleusercontent.com"
        const val CLIENT_SECRET = "ZtS_4ANT1xX3SPlNgPIMjNzW"
        val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME
    }
}