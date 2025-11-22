package app.linksheet.feature.libredirect

import app.linksheet.api.database.CrossDatabaseMigration
import app.linksheet.api.database.DefaultCrossDatabaseMigration
import app.linksheet.feature.libredirect.database.LibRedirectDatabase
import app.linksheet.feature.libredirect.database.repository.LibRedirectDefaultRepository
import app.linksheet.feature.libredirect.database.repository.LibRedirectStateRepository
import app.linksheet.feature.libredirect.viewmodel.LibRedirectServiceSettingsViewModel
import app.linksheet.feature.libredirect.viewmodel.LibRedirectSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val LibRedirectMigratorModule = module {
    single<CrossDatabaseMigration>(qualifier<LibRedirectDatabase>()) { DefaultCrossDatabaseMigration() }
}

val LibRedirectFeatureModule = module {
    includes(LibRedirectMigratorModule)
    single<LibRedirectDatabase> {
        LibRedirectDatabase.create(
            context = get(),
            name = "libredirect",
            migrator = get(qualifier<LibRedirectDatabase>())
        )
    }
    factory { LibRedirectDefaultRepository(dao = get<LibRedirectDatabase>().libRedirectDefaultDao()) }
    factory { LibRedirectStateRepository(dao = get<LibRedirectDatabase>().libRedirectServiceStateDao()) }
    factoryOf(::LibRedirectResolver)
    viewModelOf(::LibRedirectSettingsViewModel)
    viewModel { parameters ->
        LibRedirectServiceSettingsViewModel(
            context = get(),
            serviceKey = parameters.get(),
            defaultRepository = get(),
            stateRepository = get(),
        )
    }
}
