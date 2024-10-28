package fe.linksheet.module.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration13to15 : Migration(13, 15) {
    override fun migrate(db: SupportSQLiteDatabase) = db.run {
        execSQL("ALTER TABLE installed_app DROP COLUMN flags")
        execSQL("ALTER TABLE installed_app ADD COLUMN flags INTEGER DEFAULT 0 NOT NULL")
    }
}
