@file:OptIn(SensitivePreference::class, ExperimentalSerializationApi::class)

package fe.linksheet.module.viewmodel.module


import android.content.pm.getApplicationInfoCompatOrNull
import app.linksheet.api.SensitivePreference
import app.linksheet.api.SystemInfoService
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.exportimport.ExportImportUseCase
import app.linksheet.feature.profile.ProfileFeatureModule
import com.akuleshov7.ktoml.Toml
import fe.droidkit.koin.getPackageManager
import fe.gson.GsonQualifier
import fe.linksheet.module.log.DefaultLogModule
import fe.linksheet.module.repository.module.RepositoryModule
import fe.linksheet.module.resolver.SelfIntentUseCase
import fe.linksheet.module.viewmodel.*
import fe.linksheet.module.viewmodel.util.LogViewCommon
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
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
//    factory{
//        ClipboardUseCase(
//            repository = get<AppPreferenceRepository>(),
//            clipboardManager = getSystemServiceOrThrow<ClipboardManager>(),
//        )
//    }

    viewModelOf(::MainViewModel)
    factory {
        PreferredAppCompatHelper(
            creator = get(),
            getApplicationInfoOrNull = getPackageManager()::getApplicationInfoCompatOrNull,
        )
    }
    viewModelOf(::VerifiedLinkHandlersViewModel)
    viewModel { parameters ->
        VerifiedLinkHandlerViewModel(
            handle = parameters.get(),
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
    single { SelfIntentUseCase(selfPackage = get<SystemInfoService>().getApplicationId()) }
    viewModelOf(::BottomSheetViewModel)
    viewModelOf(::ClipboardProxyViewModel)
    viewModelOf(::ThemeSettingsViewModel).bind<RootViewModel>()
    viewModelOf(::LanguageSettingsViewModel)
    viewModelOf(::FollowRedirectsSettingsViewModel)
    viewModelOf(::LogSettingsViewModel)
    viewModel { parameters ->
        LogTextSettingsViewModel(
            context = get(),
            sessionId = parameters[0],
            logViewCommon = get(),
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
            shizukuStatusUseCase = get(),
            shizukuFeatureService = get(),
            miuiCompatProvider = get(),
            gson = get(qualifier(GsonQualifier.Pretty)),
            systemInfoService = get(),
            logPersistService = get(),
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
