package app.linksheet.api.database

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase

interface KnownHolder<T : IKnown> {
    val items: List<T>

    fun initialize(db: SupportSQLiteDatabase)
    fun initialize(connection: SQLiteConnection)
}
