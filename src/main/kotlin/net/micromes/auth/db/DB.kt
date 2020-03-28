package net.micromes.auth.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun dBConnect() {
    Database.connect(url = "jdbc:mysql://localhost:3306/micromes-auth", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "")
}

fun dBInit() {
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(
            Tables.Companion.Users
        )
    }
}