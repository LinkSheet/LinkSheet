package app.linksheet.feature.backup.impl

import app.linksheet.feature.backup.impl.core.BackupConfiguration
import app.linksheet.feature.backup.impl.usecase.BackupUseCase
import app.linksheet.feature.backup.impl.usecase.RestoreUseCase
import app.linksheet.feature.backup.impl.viewmodel.BackupViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


fun BackupFeatureModule(): Module {
    return module {
        factory {
            BackupUseCase(configuration = get<BackupConfiguration>())
        }
        factory {
            RestoreUseCase(configuration = get<BackupConfiguration>())
        }
        viewModel {
            BackupViewModel(
                context = get(),
                clock = get(),
                backupUseCase = get(),
                restoreUseCase = get()
            )
        }
    }
}
