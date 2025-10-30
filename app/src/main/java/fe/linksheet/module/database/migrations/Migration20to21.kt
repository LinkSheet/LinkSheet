package fe.linksheet.module.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration20to21 : Migration(20, 21) {

    override fun migrate(db: SupportSQLiteDatabase) = db.run {
        execSQL("DROP TABLE `wiki_cache`")
    }
}
