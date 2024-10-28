package fe.linksheet.module.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fe.std.result.tryCatch

object Migration13to16 : Migration(13, 16) {
    override fun migrate(db: SupportSQLiteDatabase): Unit = db.run {
        tryCatch {
            execSQL("ALTER TABLE installed_app DROP COLUMN flags")
            execSQL("ALTER TABLE installed_app ADD COLUMN flags INTEGER DEFAULT 0 NOT NULL")
        }
    }
}
