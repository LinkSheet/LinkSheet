package app.linksheet.api.database

import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.SQLITE_DATA_BLOB
import androidx.sqlite.SQLITE_DATA_FLOAT
import androidx.sqlite.SQLITE_DATA_INTEGER
import androidx.sqlite.SQLITE_DATA_NULL
import androidx.sqlite.SQLITE_DATA_TEXT
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.execSQL

private fun getConflictAlgorithmClause(conflictAlgorithm: Int) =
    when (conflictAlgorithm) {
        SQLiteDatabase.CONFLICT_NONE -> ""
        SQLiteDatabase.CONFLICT_ROLLBACK -> "OR ROLLBACK "
        SQLiteDatabase.CONFLICT_ABORT -> "OR ABORT "
        SQLiteDatabase.CONFLICT_FAIL -> "OR FAIL "
        SQLiteDatabase.CONFLICT_IGNORE -> "OR IGNORE "
        SQLiteDatabase.CONFLICT_REPLACE -> "OR REPLACE "
        else -> error("Unknown conflictAlgorithm: $conflictAlgorithm")
    }

internal fun SQLiteStatement.bindArgsArray(bindArgs: Array<out Any?>) {
    bindArgs.forEachIndexed { index, arg ->
        val bindIndex = index + 1
        when (arg) {
            null -> bindNull(bindIndex)
            is Boolean -> bindLong(bindIndex, if (arg) 1 else 0)
            is Int -> bindLong(bindIndex, arg.toLong())
            is Long -> bindLong(bindIndex, arg)
            is Float -> bindDouble(bindIndex, arg.toDouble())
            is Double -> bindDouble(bindIndex, arg)
            is String -> bindText(bindIndex, arg)
            is ByteArray -> bindBlob(bindIndex, arg)
        }
    }
}

internal fun SQLiteStatement.toCursor(): Cursor {
    val columnNames = getColumnNames().toTypedArray()
    val cursor = MatrixCursor(columnNames)
    while (step()) {
        val row =
            Array<Any?>(columnNames.size) { i ->
                val columnType = getColumnType(i)
                when (columnType) {
                    SQLITE_DATA_INTEGER -> getLong(i)
                    SQLITE_DATA_FLOAT -> getDouble(i)
                    SQLITE_DATA_TEXT -> getText(i)
                    SQLITE_DATA_BLOB -> getBlob(i)
                    SQLITE_DATA_NULL -> null
                    else -> error("Unknown column type: $columnType")
                }
            }
        cursor.addRow(row)
    }
    return cursor
}

// Attempts to mimic androidx.room3.support.RoomSupportSQLiteDatabase#query
fun SQLiteConnection.query(query: String): Cursor {
    return prepare(query).use { stmt ->
        stmt.toCursor()
    }
}



// Attempts to mimic androidx.room3.support.RoomSupportSQLiteDatabase#insert
fun SQLiteConnection.insert(table: String, conflictAlgorithm: Int, values: ContentValues): Long {
    val size = values.size()
    if (size == 0) {
        return -1
    }

    val sql = StringBuilder()
    sql.append("INSERT ")
    sql.append(getConflictAlgorithmClause(conflictAlgorithm))
    sql.append("INTO ")
    sql.append(table)
    sql.append("(")

    val bindArgs = arrayOfNulls<Any?>(size)
    var i = 0
    for (colName in values.keySet()) {
        sql.append(if (i > 0) "," else "")
        sql.append(colName)
        bindArgs[i++] = values[colName]
    }
    sql.append(")")
    sql.append(" VALUES (")
    for (j in 0 until size) {
        sql.append(if (j > 0) "," else "")
        sql.append("?")
    }
    sql.append(")")

    return executeInsert(sql.toString(), bindArgs) ?: -1
}

// Attempts to mimic androidx.room3.support.RoomSupportSQLiteStatement
fun SQLiteConnection.executeInsert(sql: String, bindArgs: Array<Any?>): Long? {
    return immediateTransaction {
        prepare(sql).use { stmt ->
            stmt.bindArgsArray(bindArgs)
            stmt.step()
        }
        getLastInsertedRowId()
    }
}


internal fun SQLiteConnection.getLastInsertedRowId(): Long {
    if (getTotalChangedRows() == 0) {
        return -1
    }
    return prepare("SELECT last_insert_rowid()").use {
        it.step()
        it.getLong(0)
    }
}

internal fun SQLiteConnection.getTotalChangedRows(): Int {
    return prepare("SELECT changes()").use {
        it.step()
        it.getLong(0).toInt()
    }
}

interface SqliteConnectionTransactionScope {

}

class SqliteConnectionTransactionScopeImpl : SqliteConnectionTransactionScope {}


fun <R> SQLiteConnection.immediateTransaction(
    block: SqliteConnectionTransactionScope.() -> R
): R? {
    val scope = SqliteConnectionTransactionScopeImpl()
    execSQL("BEGIN IMMEDIATE TRANSACTION")
    try {
        val result = block(scope)
        execSQL("END TRANSACTION")
        return result
    } catch(t: Throwable) {
        execSQL("ROLLBACK TRANSACTION")
    }
    return null
}
