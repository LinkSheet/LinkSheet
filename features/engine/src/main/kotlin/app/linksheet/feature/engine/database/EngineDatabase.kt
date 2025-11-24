package app.linksheet.feature.engine.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import app.linksheet.api.database.KnownInitCallback
import app.linksheet.feature.engine.database.dao.*
import app.linksheet.feature.engine.database.entity.*

@Database(
    entities = [
        CachedHtml::class,
        PreviewCache::class,
        ResolvedUrl::class,
        ResolveType::class,
        UrlEntry::class,
        ExpressionRule::class,
        Scenario::class,
        ScenarioExpression::class
    ],
    version = EngineDatabase.LATEST_VERSION,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
    exportSchema = true
)
abstract class EngineDatabase internal constructor() : RoomDatabase() {
    abstract fun htmlCacheDao(): HtmlCacheDao
    abstract fun previewCacheDao(): PreviewCacheDao
    abstract fun resolvedUrlCacheDao(): ResolvedUrlCacheDao
    abstract fun resolveTypeDao(): ResolveTypeDao
    abstract fun urlEntryDao(): UrlEntryDao
    abstract fun expressionRuleDao(): ExpressionRuleDao
    abstract fun scenarioDao(): ScenarioDao
    abstract fun scenarioExpressionDao(): ScenarioExpressionDao

    companion object {
        const val LATEST_VERSION = 2

        private fun buildMigrations(): Array<Migration> {
            return arrayOf(
            )
        }

        internal fun Builder<EngineDatabase>.configureAndBuild(): EngineDatabase {
            return addCallback(KnownInitCallback(ResolveType))
                .addMigrations(*buildMigrations())
                .build()
        }

        internal fun create(context: Context, name: String): EngineDatabase {
            return Room
                .databaseBuilder(context, EngineDatabase::class.java, name)
                .configureAndBuild()
        }
    }
}
