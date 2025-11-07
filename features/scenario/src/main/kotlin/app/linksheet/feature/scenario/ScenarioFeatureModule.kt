@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.scenario

import app.linksheet.feature.scenario.viewmodel.ScenarioOverviewViewModel
import app.linksheet.feature.scenario.viewmodel.ScenarioViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

val ScenarioFeatureModule = module {
    viewModelOf(::ScenarioOverviewViewModel)
    viewModel { parameters ->
        ScenarioViewModel(
            context = get(),
            scenarioRepository = get(),
            id = parameters.get(),
        )
    }
}
