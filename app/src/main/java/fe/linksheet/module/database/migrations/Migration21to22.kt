package fe.linksheet.module.database.migrations

import android.content.ContentValues
import android.database.Cursor
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.linksheet.api.database.CrossDatabaseMigration

class Migration21to22(
    private val migrator: CrossDatabaseMigration,
) : Migration(21, 22) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.use {
            migrateTable(it, "lib_redirect_default")
            migrateTable(it, "lib_redirect_service_state")
        }
    }

    private fun migrateTable(db: SupportSQLiteDatabase, table: String) {
        val cursor = db.query("SELECT * FROM $table")
        migrator.put(table, cursor.toContentValues())
    }
}

private fun Cursor.toContentValues(): List<ContentValues> {
    fun handleRow(cursor: Cursor): ContentValues {
        val cv = ContentValues()
        for (i in 0 until cursor.columnCount) {
            val name = cursor.getColumnName(i)
            when (cursor.getType(i)) {
                Cursor.FIELD_TYPE_NULL -> cv.putNull(name)
                Cursor.FIELD_TYPE_INTEGER -> cv.put(name, cursor.getLong(i))
                Cursor.FIELD_TYPE_FLOAT -> cv.put(name, cursor.getDouble(i))
                Cursor.FIELD_TYPE_STRING -> cv.put(name, cursor.getString(i))
                Cursor.FIELD_TYPE_BLOB -> cv.put(name, cursor.getBlob(i))
                else -> throw IllegalStateException()
            }
        }
        return cv
    }

    fun handleRows(cursor: Cursor): List<ContentValues> = buildList {
        while (cursor.moveToNext()) {
            add(handleRow(cursor))
        }
    }

    return use { handleRows(it) }
}
