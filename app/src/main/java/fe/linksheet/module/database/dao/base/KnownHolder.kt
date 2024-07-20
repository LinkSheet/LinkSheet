package fe.linksheet.module.database.dao.base

import androidx.sqlite.db.SupportSQLiteDatabase

interface KnownHolder<T : IKnown> {
    val items: List<T>

    fun initialize(db: SupportSQLiteDatabase)
}
