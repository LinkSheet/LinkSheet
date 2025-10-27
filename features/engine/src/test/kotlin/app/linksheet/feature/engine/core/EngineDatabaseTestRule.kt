package app.linksheet.feature.engine.core

import android.content.Context
import app.linksheet.feature.engine.database.EngineDatabase
import app.linksheet.feature.engine.database.EngineDatabase.Companion.configureAndBuild
import app.linksheet.testlib.rule.DatabaseTestRule
import org.junit.Rule


fun EngineDatabaseTestRule(applicationContext: Context): DatabaseTestRule<EngineDatabase> {
    return DatabaseTestRule<EngineDatabase>(applicationContext) { it.configureAndBuild() }
}
