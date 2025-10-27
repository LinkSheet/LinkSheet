package app.linksheet.api.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class KnownInitCallback(private vararg val knownHolders: KnownHolder<*>) : RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        for (holder in knownHolders) {
            holder.initialize(db)
        }
    }
}
