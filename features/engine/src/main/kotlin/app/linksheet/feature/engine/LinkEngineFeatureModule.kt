@file:OptIn(ExperimentalTime::class)

package app.linksheet.feature.engine

import app.linksheet.feature.engine.database.EngineDatabase
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.engine.database.repository.ScenarioRepository
import app.linksheet.feature.engine.viewmodel.ScenarioOverviewViewModel
import app.linksheet.feature.engine.viewmodel.ScenarioViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
val LinkEngineFeatureModule = module {
    single<EngineDatabase> { EngineDatabase.create(context = get(), name = "engine") }
    factory {
        val db = get<EngineDatabase>()
        CacheRepository(
            htmlCacheDao = db.htmlCacheDao(),
            previewCacheDao = db.previewCacheDao(),
            resolvedUrlCacheDao = db.resolvedUrlCacheDao(),
            resolveTypeDao = db.resolveTypeDao(),
            urlEntryDao = db.urlEntryDao(),
            clock = get()
        )
    }
    factory {
        val db = get<EngineDatabase>()
        ScenarioRepository(
            scenarioDao = db.scenarioDao(),
            expressionRuleDao = db.expressionRuleDao(),
            scenarioExpressionDao = db.scenarioExpressionDao(),
        )
    }
    viewModelOf(::ScenarioOverviewViewModel)
    viewModel { parameters ->
        ScenarioViewModel(
            context = get(),
            scenarioRepository = get(),
            id = parameters.get(),
        )
    }
}
