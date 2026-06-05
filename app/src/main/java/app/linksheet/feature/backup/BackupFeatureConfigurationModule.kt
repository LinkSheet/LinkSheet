@file:OptIn(SensitivePreference::class, ExperimentalSerializationApi::class)

package app.linksheet.feature.backup

import app.linksheet.api.SensitivePreference
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.backup.impl.core.BackupConfiguration
import app.linksheet.feature.backup.impl.core.DatabaseExportImportHolder
import app.linksheet.feature.backup.impl.core.DatabaseType
import app.linksheet.feature.backup.impl.core.PreferenceExportImportHolder
import app.linksheet.feature.backup.impl.core.PreferenceType
import app.linksheet.feature.backup.model.Amp2HtmlMappingExportModel
import app.linksheet.feature.backup.model.AppSelectionHistoryExportModel
import app.linksheet.feature.backup.model.DisableInAppBrowserInSelectedExportModel
import app.linksheet.feature.backup.model.PreferredAppExportModel
import app.linksheet.feature.backup.model.ResolvedRedirectExportModel
import app.linksheet.feature.backup.model.WhitelistedInAppBrowserExportModel
import app.linksheet.feature.backup.model.WhitelistedNormalBrowserExportModel
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.state.AppStatePreferences
import fe.linksheet.module.preference.state.DefaultAppStateRepository
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.dsl.module

val BackupFeatureConfigurationModule = module {
    single {
        BackupConfiguration(
            builder = {
                subclassesOfSealed<Amp2HtmlMappingExportModel>()
                subclassesOfSealed<AppSelectionHistoryExportModel>()
                subclassesOfSealed<DisableInAppBrowserInSelectedExportModel>()
                subclassesOfSealed<PreferredAppExportModel>()
                subclassesOfSealed<ResolvedRedirectExportModel>()
                subclassesOfSealed<WhitelistedInAppBrowserExportModel>()
                subclassesOfSealed<WhitelistedNormalBrowserExportModel>()
            },
            holders = listOf(
                PreferenceExportImportHolder(
                    type = PreferenceType.Preferences,
                    repository = get<AppPreferenceRepository>(),
                    definition = AppPreferences,
                    exclude = AppPreferences.sensitivePreferences.mapToSet { it.key }
                ),
                PreferenceExportImportHolder(
                    type = PreferenceType.Experiments,
                    repository = get<ExperimentRepository>(),
                    definition = Experiments,
                ),
                PreferenceExportImportHolder(
                    type = PreferenceType.AppState,
                    repository = get<DefaultAppStateRepository>(),
                    definition = AppStatePreferences,
                )
            ),
            databaseHolders = listOf(
                DatabaseExportImportHolder(
                    PreferenceType.Preferences,
                    get<PreferredAppRepository>()
                ),
                DatabaseExportImportHolder(
                    PreferenceType.Preferences,
                    get<DisableInAppBrowserInSelectedRepository>()
                ),
                DatabaseExportImportHolder(
                    PreferenceType.Preferences,
                    get<WhitelistedNormalBrowsersRepository>()
                ),
                DatabaseExportImportHolder(
                    PreferenceType.Preferences,
                    get<WhitelistedInAppBrowsersRepository>()
                ),
                DatabaseExportImportHolder(
                    DatabaseType.SelectionHistory,
                    get<AppSelectionHistoryRepository>()
                ),
                DatabaseExportImportHolder(DatabaseType.Cache, get<ResolvedRedirectRepository>()),
                DatabaseExportImportHolder(DatabaseType.Cache, get<Amp2HtmlRepository>())
            ),
        )
    }
}
