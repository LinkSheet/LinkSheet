package fe.linksheet.module.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration20to21 : Migration(20, 21) {

    override fun migrate(db: SupportSQLiteDatabase) = db.run {
        execSQL("DROP TABLE `html_cache`")
        execSQL("DROP TABLE `preview_cache`")
        execSQL("DROP TABLE `resolved_url`")
        execSQL("DROP TABLE `resolve_type`")
        execSQL("DROP TABLE `url`")
    }
}
