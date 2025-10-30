package fe.linksheet.module.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration19to20 : Migration(19, 20) {

    override fun migrate(db: SupportSQLiteDatabase) = db.run {
        execSQL("DROP TABLE `html_cache`")
        execSQL("DROP TABLE `preview_cache`")
        execSQL("DROP TABLE `resolved_url`")
        execSQL("DROP TABLE `resolve_type`")
        execSQL("DROP TABLE `url`")
    }
}
