package net.micromes.auth.db

import net.micromes.auth.db.Tables.Companion.Users
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class DBUser {

    fun getUserIDByExternalID(googleID: String): Long? {
        var userID: Long? = null
        transaction {
            Users.select { Users.googleID eq googleID }.forEach {
                userID = it[Users.id].value
            }
        }
        return userID
    }
}