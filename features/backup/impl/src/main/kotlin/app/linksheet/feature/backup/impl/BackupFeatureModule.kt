package app.linksheet.feature.backup.impl

import app.linksheet.feature.backup.impl.viewmodel.ExportSettingsViewModel2
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val BackupFeatureModule = module {

    viewModel {
        ExportSettingsViewModel2(
            context = get(),
            clock = get(),
            useCase = get()
        )
    }
}
