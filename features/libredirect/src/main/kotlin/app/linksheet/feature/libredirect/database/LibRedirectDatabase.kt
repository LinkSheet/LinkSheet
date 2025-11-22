package app.linksheet.feature.libredirect.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import app.linksheet.api.database.CrossDatabaseMigrationCallback
import app.linksheet.api.database.CrossDatabaseMigration
import app.linksheet.feature.libredirect.database.dao.LibRedirectDefaultDao
import app.linksheet.feature.libredirect.database.dao.LibRedirectServiceStateDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import app.linksheet.feature.libredirect.database.entity.LibRedirectServiceState

@Database(
    entities = [
        LibRedirectDefault::class,
        LibRedirectServiceState::class,
    ],
    version = 1,
)
abstract class LibRedirectDatabase internal constructor() : RoomDatabase() {
    abstract fun libRedirectDefaultDao(): LibRedirectDefaultDao
    abstract fun libRedirectServiceStateDao(): LibRedirectServiceStateDao

    companion object {
        private fun buildMigrations(): Array<Migration> {
            return arrayOf(
            )
        }

        internal fun Builder<LibRedirectDatabase>.configureAndBuild(): LibRedirectDatabase {
            return addMigrations(*buildMigrations())
                .build()
        }

        internal fun create(
            context: Context,
            name: String,
            migrator: CrossDatabaseMigration
        ): LibRedirectDatabase {
            val builder = Room.databaseBuilder(context, LibRedirectDatabase::class.java, name)
            builder.addCallback(CrossDatabaseMigrationCallback(migrator))

            return builder.configureAndBuild()
        }
    }
}
