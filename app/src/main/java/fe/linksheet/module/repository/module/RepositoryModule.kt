package fe.linksheet.module.repository.module

import app.linksheet.api.preference.AppPreferenceRepository
import fe.composekit.preference.asFunction
import fe.linksheet.module.clock.ClockModule
import fe.linksheet.module.database.DatabaseModule
import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

val RepositoryModule = module {
    includes(ClockModule, DatabaseModule)

    factory {
        val appPreferenceRepository = get<AppPreferenceRepository>()
        PreferredAppRepository(
            dao = get<LinkSheetDatabase>().preferredAppDao(),
            incognitoMode = appPreferenceRepository.asFunction(AppPreferences.privacy.incognito.enable)
        )
    }
    factory { DisableInAppBrowserInSelectedRepository(dao = get<LinkSheetDatabase>().disableInAppBrowserInSelectedDao()) }
    factory { WhitelistedNormalBrowsersRepository(dao = get<LinkSheetDatabase>().whitelistedBrowsersDao()) }
    factory { WhitelistedInAppBrowsersRepository(dao = get<LinkSheetDatabase>().whitelistedInAppBrowsersDao()) }
    factory {
        val appPreferenceRepository = get<AppPreferenceRepository>()
        AppSelectionHistoryRepository(
            dao = get<LinkSheetDatabase>().appSelectionHistoryDao(),
            incognitoMode = appPreferenceRepository.asFunction(AppPreferences.privacy.incognito.enable)
        )
    }
    factory {
        val appPreferenceRepository = get<AppPreferenceRepository>()
        ResolvedRedirectRepository(
            dao = get<LinkSheetDatabase>().resolvedRedirectDao(),
            incognitoMode = appPreferenceRepository.asFunction(AppPreferences.privacy.incognito.enable)
        )
    }
    factory {
        val appPreferenceRepository = get<AppPreferenceRepository>()
        Amp2HtmlRepository(
            dao = get<LinkSheetDatabase>().amp2HtmlMappingDao(),
            incognitoMode = appPreferenceRepository.asFunction(AppPreferences.privacy.incognito.enable)
        )
    }
}
