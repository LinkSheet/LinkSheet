package fe.linksheet.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.android.material.snackbar.Snackbar
import fe.linksheet.*
import fe.linksheet.composable.main.MainRoute
import fe.linksheet.composable.settings.SettingsRoute
import fe.linksheet.composable.settings.about.AboutSettingsRoute
import fe.linksheet.composable.settings.about.CreditsSettingsRoute
import fe.linksheet.composable.settings.about.DonateSettingsRoute
import fe.linksheet.composable.settings.advanced.*
import fe.linksheet.composable.settings.apps.AppsSettingsRoute
import fe.linksheet.composable.settings.apps.PretendToBeAppSettingsRoute
import fe.linksheet.composable.settings.apps.link.AppsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.settings.apps.preferred.PreferredAppSettingsRoute
import fe.linksheet.composable.settings.bottomsheet.BottomSheetSettingsRoute
import fe.linksheet.composable.settings.browser.BrowserSettingsRoute
import fe.linksheet.composable.settings.browser.inapp.InAppBrowserSettingsDisableInSelectedRoute
import fe.linksheet.composable.settings.browser.inapp.InAppBrowserSettingsRoute
import fe.linksheet.composable.settings.browser.mode.PreferredBrowserSettingsRoute
import fe.linksheet.composable.settings.browser.mode.WhitelistedBrowsersSettingsRoute
import fe.linksheet.composable.settings.debug.DebugSettingsRoute
import fe.linksheet.composable.settings.debug.loadpreferences.LoadDumpedPreferences
import fe.linksheet.composable.settings.debug.log.LogSettingsRoute
import fe.linksheet.composable.settings.debug.log.LogTextSettingsRoute
import fe.linksheet.composable.settings.dev.DevBottomSheetSettingsRoute
import fe.linksheet.composable.settings.dev.DevSettingsRoute
import fe.linksheet.composable.settings.general.GeneralSettingsRoute
import fe.linksheet.composable.settings.link.LinksSettingsRoute
import fe.linksheet.composable.settings.link.amp2html.Amp2HtmlSettingsRoute
import fe.linksheet.composable.settings.link.downloader.DownloaderSettingsRoute
import fe.linksheet.composable.settings.link.libredirect.LibRedirectServiceSettingsRoute
import fe.linksheet.composable.settings.link.libredirect.LibRedirectSettingsRoute
import fe.linksheet.composable.settings.link.redirect.FollowRedirectsSettingsRoute
import fe.linksheet.composable.settings.notification.NotificationSettingsRoute
import fe.linksheet.composable.settings.privacy.PrivacySettingsRoute
import fe.linksheet.composable.settings.theme.ThemeSettingsRoute
import fe.linksheet.composable.util.animatedArgumentRouteComposable
import fe.linksheet.composable.util.animatedComposable
import fe.linksheet.debug.DebugIntentHandler
import fe.linksheet.extension.android.initPadding
import fe.linksheet.extension.compose.setContentWithKoin
import fe.linksheet.module.analytics.AnalyticsEvent
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.AppHost
import fe.linksheet.ui.findWindow
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.BuildType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModel<MainViewModel>()
    private val app by inject<LinkSheetApp>()



//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        if (mainViewModel.firstRun.value && BuildConfig.BUILD_TYPE == "release") {
//            startActivity(Intent(this, OnboardingActivity::class.java))
//            finish()
//

//        if (BuildConfig.BUILD_TYPE == "debug") {
//            startActivity(Intent(this, NewOnboardingActivity::class.java))
//            finish()
//        }

//        Intent.ACTION_WEB_SEARCH
        initPadding()
        setContentWithKoin()

        if (intent != null && BuildConfig.DEBUG && BuildType.current == BuildType.Debug) {
            DebugIntentHandler.onCreateMainActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        app.setActivityEventListener(null)
    }

    private fun setContentWithKoin() {
        setContentWithKoin {
            val navController = rememberNavController()
            if (BuildConfig.DEBUG) {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    Log.d("Analytics", "Enqueuing nav event to ${destination.route}")
                    mainViewModel.analyticsClient.enqueue(AnalyticsEvent.Navigate(destination.route ?: "<no_route>"))
                }

                app.setActivityEventListener {
//                    Snackbar.make(window.decorView, it.toString(), Snackbar.LENGTH_SHORT).show()
                }
            }

            AppHost {
                NavHost(
                    navController = navController,
                    startDestination = mainRoute,
                ) {
                    animatedComposable(route = mainRoute) {
                        MainRoute(navController = navController)
                    }

                    val onBackPressed: () -> Unit = { navController.popBackStack() }
                    animatedComposable(route = settingsRoute) {
                        SettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = appsSettingsRoute) {
                        AppsSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = browserSettingsRoute) {
                        BrowserSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = generalSettingsRoute) {
                        GeneralSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = notificationSettingsRoute) {
                        NotificationSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = privacySettingsRoute) {
                        PrivacySettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = bottomSheetSettingsRoute) {
                        BottomSheetSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = linksSettingsRoute) {
                        LinksSettingsRoute(
                            onBackPressed = onBackPressed,
                            navController = navController,
                        )
                    }

                    animatedComposable(route = followRedirectsSettingsRoute) {
                        FollowRedirectsSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = libRedirectSettingsRoute) {
                        LibRedirectSettingsRoute(
                            onBackPressed = onBackPressed,
                            navController = navController,
                        )
                    }

                    animatedArgumentRouteComposable(route = libRedirectServiceSettingsRoute) { _, _ ->
                        LibRedirectServiceSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = downloaderSettingsRoute) {
                        DownloaderSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = amp2HtmlSettingsRoute) {
                        Amp2HtmlSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = themeSettingsRoute) {
                        ThemeSettingsRoute(navController = navController)
                    }

                    animatedComposable(route = advancedSettingsRoute) {
                        AdvancedSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = shizukuSettingsRoute) {
                        ShizukuSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = featureFlagSettingsRoute) {
                        FeatureFlagSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = experimentSettingsRoute) {
                        ExperimentsSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = exportImportSettingsRoute) {
                        ExportImportSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = debugSettingsRoute) {
                        DebugSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = logViewerSettingsRoute) {
                        LogSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = loadDumpedPreferences) {
                        LoadDumpedPreferences(onBackPressed = onBackPressed)
                    }

                    animatedArgumentRouteComposable(route = logTextViewerSettingsRoute) { _, _ ->
                        LogTextSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = aboutSettingsRoute) {
                        AboutSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = donateSettingsRoute) {
                        DonateSettingsRoute(
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = creditsSettingsRoute) {
                        CreditsSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = preferredBrowserSettingsRoute) {
                        PreferredBrowserSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = whitelistedBrowsersSettingsRoute) {
                        WhitelistedBrowsersSettingsRoute(navController = navController)
                    }

                    animatedComposable(route = inAppBrowserSettingsRoute) {
                        InAppBrowserSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    animatedComposable(route = inAppBrowserSettingsDisableInSelectedRoute) {
                        InAppBrowserSettingsDisableInSelectedRoute(navController = navController)
                    }

                    animatedComposable(route = preferredAppsSettingsRoute) {
                        PreferredAppSettingsRoute(onBackPressed = onBackPressed)
                    }

                    if (AndroidVersion.AT_LEAST_API_31_S) {
                        animatedComposable(route = appsWhichCanOpenLinksSettingsRoute) {
                            AppsWhichCanOpenLinksSettingsRoute(onBackPressed = onBackPressed)
                        }

                        animatedComposable(route = pretendToBeAppRoute) {
                            PretendToBeAppSettingsRoute(onBackPressed = onBackPressed)
                        }
                    }

                    animatedComposable(route = devModeRoute) {
                        DevSettingsRoute(onBackPressed = onBackPressed)
                    }

                    animatedComposable(route = devBottomSheetExperimentRoute) {
                        DevBottomSheetSettingsRoute(onBackPressed = onBackPressed)
                    }
                }
            }
        }
    }
}
