package net.micromes.auth.db

import net.micromes.auth.db.Tables.Companion.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
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

    fun checkExternalIDExits(externalID: String) : Boolean {
        var exists: Boolean = false
        transaction {
            Users.select { Users.googleID eq externalID }.forEach {
                exists = true
            }
        }
        return exists
    }

    fun createUserMappingAndReturnID(externalID: String) : Long {
        var id : Long? = null
        transaction {
            id = Users.insertAndGetId {
                it[Users.googleID] = externalID
            }.value
        }
        return id ?: throw Error()
    }
}