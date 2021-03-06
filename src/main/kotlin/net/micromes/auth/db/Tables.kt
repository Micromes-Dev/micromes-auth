package net.micromes.auth.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table

class Tables {
    companion object {
        object Users : LongIdTable() {
            val googleID = varchar("googleid", 21).uniqueIndex()
        }
    }
}