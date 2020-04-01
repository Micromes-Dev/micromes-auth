package net.micromes.auth.db

import net.micromes.auth.config.Settings
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun dBConnect() {
    Database.connect(url="jdbc:mysql://localhost:3306/micromes-auth?autoReconnect=false&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin&allowPublicKeyRetrieval=true", driver="com.mysql.cj.jdbc.Driver", user = Settings.DB_USER, password = Settings.DB_PASSWORD)
}

fun dBInit() {
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(
            Tables.Companion.Users
        )
    }
}