package app.linksheet.feature.libredirect.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import app.linksheet.api.database.CrossDatabaseMigration
import app.linksheet.api.database.CrossDatabaseMigrationCallback
import app.linksheet.feature.libredirect.database.dao.LibRedirectDefaultDao
import app.linksheet.feature.libredirect.database.dao.LibRedirectServiceStateDao
import app.linksheet.feature.libredirect.database.dao.LibRedirectUserInstanceDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import app.linksheet.feature.libredirect.database.entity.LibRedirectServiceState
import app.linksheet.feature.libredirect.database.entity.LibRedirectUserInstance

@Database(
    entities = [
        LibRedirectDefault::class,
        LibRedirectServiceState::class,
        LibRedirectUserInstance::class
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    version = 2,
)
abstract class LibRedirectDatabase internal constructor() : RoomDatabase() {
    abstract fun defaultDao(): LibRedirectDefaultDao
    abstract fun serviceStateDao(): LibRedirectServiceStateDao
    abstract fun userInstanceDao(): LibRedirectUserInstanceDao

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
