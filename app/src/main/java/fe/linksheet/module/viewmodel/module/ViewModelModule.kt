@file:OptIn(SensitivePreference::class, ExperimentalSerializationApi::class)

package fe.linksheet.module.viewmodel.module


import app.linksheet.api.SensitivePreference
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.backup.api.ExportModel
import app.linksheet.feature.backup.impl.usecase.DatabaseExportImportHolder
import app.linksheet.feature.backup.impl.usecase.DatabaseType
import app.linksheet.feature.backup.impl.usecase.ExportImportUseCase2
import app.linksheet.feature.backup.impl.usecase.PreferenceExportImportHolder
import app.linksheet.feature.backup.impl.usecase.PreferenceType
import app.linksheet.feature.backup.model.Amp2HtmlMappingExportModel
import app.linksheet.feature.backup.model.AppSelectionHistoryExportModel
import app.linksheet.feature.backup.model.DisableInAppBrowserInSelectedExportModel
import app.linksheet.feature.backup.model.PreferredAppExportModel
import app.linksheet.feature.backup.model.ResolvedRedirectExportModel
import app.linksheet.feature.backup.model.WhitelistedInAppBrowserExportModel
import app.linksheet.feature.backup.model.WhitelistedNormalBrowserExportModel
import app.linksheet.feature.exportimport.ExportImportUseCase
import app.linksheet.feature.profile.ProfileFeatureModule
import com.akuleshov7.ktoml.Toml
import fe.gson.GsonQualifier
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.module.log.DefaultLogModule
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.state.AppStatePreferences
import fe.linksheet.module.preference.state.DefaultAppStateRepository
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.module.RepositoryModule
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.viewmodel.AboutSettingsViewModel
import fe.linksheet.module.viewmodel.Amp2HtmlSettingsViewModel
import fe.linksheet.module.viewmodel.AppConfigViewModel
import fe.linksheet.module.viewmodel.BottomSheetSettingsViewModel
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.module.viewmodel.CrashHandlerViewerViewModel
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import fe.linksheet.module.viewmodel.ExperimentsViewModel
import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import fe.linksheet.module.viewmodel.FollowRedirectsSettingsViewModel
import fe.linksheet.module.viewmodel.GeneralSettingsViewModel
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import fe.linksheet.module.viewmodel.LanguageSettingsViewModel
import fe.linksheet.module.viewmodel.LinksSettingsViewModel
import fe.linksheet.module.viewmodel.LoadDumpedPreferencesViewModel
import fe.linksheet.module.viewmodel.LogSettingsViewModel
import fe.linksheet.module.viewmodel.LogTextSettingsViewModel
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.module.viewmodel.NotificationSettingsViewModel
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.module.viewmodel.PretendToBeAppSettingsViewModel
import fe.linksheet.module.viewmodel.PreviewSettingsViewModel
import fe.linksheet.module.viewmodel.PrivacySettingsViewModel
import fe.linksheet.module.viewmodel.RootViewModel
import fe.linksheet.module.viewmodel.SelectDomainsConfirmationViewModel
import fe.linksheet.module.viewmodel.SettingsViewModel
import fe.linksheet.module.viewmodel.SingleBrowserViewModel
import fe.linksheet.module.viewmodel.SqlViewModel
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.module.viewmodel.VerifiedLinkHandlerViewModel
import fe.linksheet.module.viewmodel.VerifiedLinkHandlersViewModel
import fe.linksheet.module.viewmodel.WhitelistedBrowsersViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val ViewModelModule = module {
    includes(
//        PreferenceRepositoryModule,
        RepositoryModule,
        DefaultLogModule,
        ProfileFeatureModule
    )
    factory {
        LogViewCommon(
            preferenceRepository = get(),
            experimentRepository = get(),
            pasteService = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            toml = Toml.Default,
            systemInfoService = get(),
            useCase = get()
        )
    }
    factory {
        ExportImportUseCase(
            repository = get<AppPreferenceRepository>(),
            json = Json.Default,
            toml = Toml.Default,
            ioDispatcher = Dispatchers.IO
        )
    }
    factory {
        val module = SerializersModule {
            polymorphic(ExportModel::class) {
                subclassesOfSealed<Amp2HtmlMappingExportModel>()
                subclassesOfSealed<AppSelectionHistoryExportModel>()
                subclassesOfSealed<DisableInAppBrowserInSelectedExportModel>()
                subclassesOfSealed<PreferredAppExportModel>()
                subclassesOfSealed<ResolvedRedirectExportModel>()
                subclassesOfSealed<WhitelistedInAppBrowserExportModel>()
                subclassesOfSealed<WhitelistedNormalBrowserExportModel>()
            }
        }
        ExportImportUseCase2(
            json = Json { serializersModule = module },
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
                DatabaseExportImportHolder(PreferenceType.Preferences , get<PreferredAppRepository>()),
                DatabaseExportImportHolder(PreferenceType.Preferences , get<DisableInAppBrowserInSelectedRepository>()),
                DatabaseExportImportHolder(PreferenceType.Preferences , get<WhitelistedNormalBrowsersRepository>()),
                DatabaseExportImportHolder(PreferenceType.Preferences , get<WhitelistedInAppBrowsersRepository>()),
                DatabaseExportImportHolder(DatabaseType.SelectionHistory , get<AppSelectionHistoryRepository>()),
                DatabaseExportImportHolder(DatabaseType.Cache , get<ResolvedRedirectRepository>()),
                DatabaseExportImportHolder(DatabaseType.Cache, get<Amp2HtmlRepository>())
            ),
            ioDispatcher = Dispatchers.IO
        )
    }
//    factory{
//        ClipboardUseCase(
//            repository = get<AppPreferenceRepository>(),
//            clipboardManager = getSystemServiceOrThrow<ClipboardManager>(),
//        )
//    }

    viewModelOf(::MainViewModel)
    viewModelOf(::VerifiedLinkHandlersViewModel)
    viewModel { parameters ->
        VerifiedLinkHandlerViewModel(
            packageName = parameters.get(),
            preferenceRepository = get(),
            preferredAppRepository = get(),
            service = get(),
            intentCompat = get()
        )
    }
    viewModelOf(::InAppBrowserSettingsViewModel)
    viewModelOf(::PreferredBrowserViewModel)
    viewModelOf(::BottomSheetSettingsViewModel)
    viewModelOf(::LinksSettingsViewModel)
    viewModelOf(::BottomSheetViewModel)
    viewModelOf(::ThemeSettingsViewModel).bind<RootViewModel>()
    viewModelOf(::LanguageSettingsViewModel)
    viewModelOf(::FollowRedirectsSettingsViewModel)
    viewModelOf(::LogSettingsViewModel)
    viewModel { parameters ->
        LogTextSettingsViewModel(
            context = get(),
            sessionId = parameters[0],
            logViewCommon = get(),
            preferenceRepository = get(),
            logPersistService = get()
        )
    }
    viewModelOf(::CrashHandlerViewerViewModel)
    viewModelOf(::Amp2HtmlSettingsViewModel)
    viewModelOf(::FeatureFlagViewModel)
    viewModelOf(::PretendToBeAppSettingsViewModel)
    viewModelOf(::GeneralSettingsViewModel)
    viewModelOf(::LoadDumpedPreferencesViewModel)
    viewModelOf(::PrivacySettingsViewModel)

    viewModel {
        AboutSettingsViewModel(
            context = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            preferenceRepository = get(),
            infoService = get()
        )
    }
    viewModel {
        DevSettingsViewModel(
            context = get(),
            preferenceRepository = get(),
            experimentRepository = get(),
//            shizukuHandler = get(),
            miuiCompatProvider = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            systemInfoService = get(),
            logPersistService = get(),
            refineWrapper = get(),
            ioDispatcher = Dispatchers.IO
        )
    }
    viewModelOf(::SettingsViewModel)
    viewModelOf(::NotificationSettingsViewModel)
    viewModelOf(::ExperimentsViewModel)
    viewModelOf(::AppConfigViewModel)
    viewModelOf(::SqlViewModel)
    viewModelOf(::PreviewSettingsViewModel)
    viewModel { parameters ->
        WhitelistedBrowsersViewModel(
            type = parameters.get(),
            useCase = get(),
            normalBrowsersRepository = get(),
            inAppBrowsersRepository = get(),
            preferenceRepository = get()
        )
    }
    viewModel { parameters ->
        SingleBrowserViewModel(
            type = parameters.get(),
            useCase = get(),
            preferenceRepository = get()
        )
    }
    viewModelOf(::SelectDomainsConfirmationViewModel)
}
