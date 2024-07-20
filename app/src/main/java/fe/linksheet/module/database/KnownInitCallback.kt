package fe.linksheet.module.database

import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import fe.linksheet.module.database.dao.base.KnownHolder

class KnownInitCallback(private vararg val knownHolders: KnownHolder<*>) : Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        for (holder in knownHolders) {
            holder.initialize(db)
        }
    }
}
