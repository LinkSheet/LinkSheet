package fe.linksheet.module.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fe.std.result.tryCatch

object Migration13to15 : Migration(13, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        tryCatch {
            db.execSQL("ALTER TABLE installed_app DROP COLUMN flags")
        }

        tryCatch {
            db.execSQL("ALTER TABLE installed_app ADD COLUMN flags INTEGER DEFAULT 0 NOT NULL")
        }
    }
}

object Migration14to15 : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        tryCatch {
            db.execSQL("ALTER TABLE installed_app DROP COLUMN flags")
        }

        tryCatch {
            db.execSQL("ALTER TABLE installed_app ADD COLUMN flags INTEGER DEFAULT 0 NOT NULL")
        }
    }
}
