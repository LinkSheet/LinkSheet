package fe.linksheet.feature.sql

import android.database.Cursor
import androidx.core.database.getBlobOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.sqlite.db.SupportSQLiteOpenHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class SqlQueryFeature(val openHelper: SupportSQLiteOpenHelper) {
    fun query(query: String): Flow<SqlRow> {
        val flow = openHelper.writableDatabase.query(query).collect()
        return flow
    }

    private fun Cursor.collect() = flow {
        use { it.collect(this@flow) }
    }

    private suspend fun Cursor.collect(collector: FlowCollector<SqlRow>) {
        if (isBeforeFirst) moveToFirst()
        if (isAfterLast) return
        do {
            collector.emit(handleRow())
        } while (moveToNext())
    }

    private fun Cursor.handleRow(): SqlRow {
        val columns = (0 until columnCount).associate { idx ->
            val name = getColumnName(idx)
            val column = handleType(idx)

            name to column
        }

        return SqlRow(columns)
    }

    private fun Cursor.handleType(idx: Int): Column<*> {
        val type = getType(idx)
        return when (type) {
            Cursor.FIELD_TYPE_NULL -> Column.Null
            Cursor.FIELD_TYPE_INTEGER -> Column.IntValue(getIntOrNull(idx))
            Cursor.FIELD_TYPE_FLOAT -> Column.FloatValue(getFloatOrNull(idx))
            Cursor.FIELD_TYPE_STRING -> Column.StringValue(getStringOrNull(idx))
            Cursor.FIELD_TYPE_BLOB -> Column.BlobValue(getBlobOrNull(idx))
            else -> error("Unreachable!")
        }
    }
}

data class SqlRow(val columns: Map<String, Column<*>>) {
    constructor(vararg columns: Pair<String, Column<*>>) : this(columns.toMap())
}

sealed interface Column<T> {
    val value: T?

    data object Null : Column<Nothing> {
        override val value: Nothing? = null
    }

    data class IntValue(override val value: Int?) : Column<Int>
    data class FloatValue(override val value: Float?) : Column<Float>
    data class StringValue(override val value: String?) : Column<String>
    data class BlobValue(override val value: ByteArray?) : Column<ByteArray>
}

