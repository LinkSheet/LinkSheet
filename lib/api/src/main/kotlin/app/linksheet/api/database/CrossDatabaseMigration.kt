package app.linksheet.api.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

interface CrossDatabaseMigration {
    fun put(table: String, contentValueLists: List<ContentValues>)
    fun get(table: String): List<ContentValues>?
    fun getTables(): List<String>
}

class DefaultCrossDatabaseMigration() : CrossDatabaseMigration {
    private val map: MutableMap<String, List<ContentValues>> = mutableMapOf()

    override fun put(table: String, contentValueLists: List<ContentValues>) {
        map[table] = contentValueLists
    }

    override fun get(table: String): List<ContentValues>? {
        return map[table]
    }

    override fun getTables(): List<String> {
        return map.keys.toList()
    }
}

class CrossDatabaseMigrationCallback(
    private vararg val migrators: CrossDatabaseMigration
) : RoomDatabase.Callback() {

    override fun onOpen(db: SupportSQLiteDatabase) {
        for (migrator in migrators) {
            migrate(db, migrator)
        }
    }

    private fun migrate(db: SupportSQLiteDatabase, migrator: CrossDatabaseMigration) {
        val tables = migrator.getTables()
        for (table in tables) {
            val contentValues = migrator.get(table) ?: continue
            for (values in contentValues) {
                db.insert(table, SQLiteDatabase.CONFLICT_REPLACE, values)
            }
        }
    }
}
