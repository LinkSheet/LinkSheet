package app.linksheet.api.database

import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection

class KnownInitCallback(private vararg val knownHolders: KnownHolder<*>) : RoomDatabase.Callback() {
    override suspend fun onOpen(connection: SQLiteConnection) {
        for (holder in knownHolders) {
            holder.initialize(connection)
        }
    }
}
