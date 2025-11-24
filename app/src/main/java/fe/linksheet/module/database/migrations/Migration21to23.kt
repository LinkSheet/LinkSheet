package fe.linksheet.module.database.migrations

import android.content.ContentValues
import android.database.Cursor
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.linksheet.api.database.CrossDatabaseMigration
import fe.linksheet.module.log.Logger
import fe.std.result.isFailure
import fe.std.result.tryCatch

class Migration21to23(
    private val logger: Logger,
    private val migrator: CrossDatabaseMigration,
) {
    companion object {
        private const val START = 21
        private const val END = 23
    }

    fun create(): Array<Migration> {
        return Array(END - START) { idx ->
            createMigration(START + idx)
        }
    }

    private fun createMigration(start: Int): Migration {
        return Migration(start, start + 1) { db ->
            logger.info("Running migration from $start to ${start + 1}")
            val result = tryCatch {
                migrateTable(db, "lib_redirect_default")
                migrateTable(db, "lib_redirect_service_state")
            }
            if (result.isFailure()) {
                logger.error(result.exception)
            }
        }
    }

    private fun migrateTable(db: SupportSQLiteDatabase, table: String) {
        val cursor = db.query("SELECT * FROM `$table`")
        migrator.put(table, cursor.toContentValues())
        db.execSQL("DROP TABLE IF EXISTS `$table`")
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
