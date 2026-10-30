package app.linksheet.feature.engine.database

import android.content.Context
import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import app.linksheet.api.database.KnownInitCallback
import app.linksheet.feature.engine.database.dao.ExpressionRuleDao
import app.linksheet.feature.engine.database.dao.HtmlCacheDao
import app.linksheet.feature.engine.database.dao.PreviewCacheDao
import app.linksheet.feature.engine.database.dao.ResolveTypeDao
import app.linksheet.feature.engine.database.dao.ResolvedUrlCacheDao
import app.linksheet.feature.engine.database.dao.ScenarioDao
import app.linksheet.feature.engine.database.dao.ScenarioExpressionDao
import app.linksheet.feature.engine.database.dao.UrlEntryDao
import app.linksheet.feature.engine.database.entity.CachedHtml
import app.linksheet.feature.engine.database.entity.ExpressionRule
import app.linksheet.feature.engine.database.entity.PreviewCache
import app.linksheet.feature.engine.database.entity.ResolveType
import app.linksheet.feature.engine.database.entity.ResolvedUrl
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.engine.database.entity.ScenarioExpression
import app.linksheet.feature.engine.database.entity.UrlEntry

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
        AutoMigration(from = 2, to = 3),
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
        const val LATEST_VERSION = 3

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
