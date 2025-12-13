package fe.linksheet.module.database.migrations

import androidx.room.migration.Migration
import mozilla.components.support.base.log.logger.Logger

class Migration12to17(private val logger: Logger) {
    companion object {
        private const val START = 12
        private const val END = 17
    }

    fun create(): Array<Migration> {
        return Array(END - START) { idx ->
            createMigration(START + idx)
        }
    }

    private fun createMigration(start: Int): Migration {
        return Migration(start, start + 1) { db ->
            logger.info("Running migration from $start to ${start + 1}")

            runCatching { db.execSQL("DROP TABLE IF EXISTS installed_app") }.onFailure { logger.error("Migration failed", it) }
            runCatching { db.execSQL("DROP TABLE IF EXISTS app_domain_verification_state") }.onFailure { logger.error("Migration failed", it) }
        }
    }
}


