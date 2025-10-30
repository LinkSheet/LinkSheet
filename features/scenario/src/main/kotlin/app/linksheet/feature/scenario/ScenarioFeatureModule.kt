package app.linksheet.feature.scenario

import app.linksheet.feature.scenario.viewmodel.ScenarioViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ScenarioFeatureModule = module {
    viewModelOf(::ScenarioViewModel)
}
