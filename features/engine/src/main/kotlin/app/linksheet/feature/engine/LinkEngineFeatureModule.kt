@file:OptIn(ExperimentalTime::class)

package app.linksheet.feature.engine

import app.linksheet.feature.engine.database.EngineDatabase
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.engine.database.repository.ScenarioRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

val LinkEngineFeatureModule = module {
    single<EngineDatabase> { EngineDatabase.create(context = get(), name = "engine") }
    singleOf(EngineDatabase::htmlCacheDao)
    singleOf(EngineDatabase::previewCacheDao)
    singleOf(EngineDatabase::resolvedUrlCacheDao)
    singleOf(EngineDatabase::resolveTypeDao)
    singleOf(EngineDatabase::urlEntryDao)
    singleOf(EngineDatabase::expressionRuleDao)
    singleOf(EngineDatabase::scenarioDao)
    singleOf(EngineDatabase::scenarioExpressionDao)
    singleOf(::CacheRepository)
    single {
        ScenarioRepository(scenarioDao = get(), expressionRuleDao = get(), scenarioExpressionDao = get())
    }
}
