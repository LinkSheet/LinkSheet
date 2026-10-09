package fe.linksheet.module.database.migrations

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

object Migration20to21 : Migration(20, 21) {

    override suspend fun migrate(connection: SQLiteConnection) = connection.run {
        execSQL("DROP TABLE `wiki_cache`")
    }
}
