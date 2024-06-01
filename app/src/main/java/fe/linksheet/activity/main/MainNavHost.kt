package fe.linksheet.activity.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
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
import fe.linksheet.experiment.ui.overhaul.composable.page.main.NewMainRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.NewSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.about.NewAboutSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.about.NewCreditsSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.advanced.NewAdvancedSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.advanced.NewExperimentsSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.app.VerifiedLinkHandlersRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.browser.NewBrowserSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.browser.inapp.NewInAppBrowserSettingsDisableInSelectedRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.browser.inapp.NewInAppBrowserSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.NewDebugSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.log.NewLogSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.log.NewLogTextSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.link.NewLinksSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.misc.MiscSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.notification.NewNotificationSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.page.settings.shortcuts.ShortcutsRoute
import fe.linksheet.util.AndroidVersion

@Composable
fun MainNavHost(
    // TODO: Refactor navController away
    navController: NavHostController,
    uiOverhaul: Boolean,
    navigate: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    NavHost(navController = navController, startDestination = mainRoute) {
        animatedComposable(route = mainRoute) {
            if (uiOverhaul) {
                NewMainRoute(navController = navController)
            } else {
                MainRoute(navController = navController)
            }
        }

        animatedComposable(route = settingsRoute) {
            if (uiOverhaul) {
                NewSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
            } else {
                SettingsRoute(navController = navController, onBackPressed = onBackPressed)
            }
        }

        animatedComposable(route = appsSettingsRoute) {
            AppsSettingsRoute(
                navController = navController, onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = browserSettingsRoute) {
            if (uiOverhaul) {
                NewBrowserSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
            } else {
                BrowserSettingsRoute(
                    navController = navController, onBackPressed = onBackPressed
                )
            }
        }

        animatedComposable(route = generalSettingsRoute) {
            if (uiOverhaul) {
                MiscSettingsRoute(onBackPressed = onBackPressed)
            } else {
                GeneralSettingsRoute(onBackPressed = onBackPressed)
            }
        }

        animatedComposable(route = notificationSettingsRoute) {
            if (uiOverhaul) {
                NewNotificationSettingsRoute(onBackPressed = onBackPressed)
            } else {
                NotificationSettingsRoute(onBackPressed = onBackPressed)
            }
        }

        animatedComposable(route = privacySettingsRoute) {
            PrivacySettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = bottomSheetSettingsRoute) {
            BottomSheetSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = linksSettingsRoute) {
            if (uiOverhaul) {
                NewLinksSettingsRoute(
                    onBackPressed = onBackPressed,
                    navigate = navigate
                )
            } else {
                LinksSettingsRoute(
                    onBackPressed = onBackPressed,
                    navController = navController,
                )
            }
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
            ThemeSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = advancedSettingsRoute) {
            if (uiOverhaul) {
                NewAdvancedSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
            } else {
                AdvancedSettingsRoute(
                    navController = navController, onBackPressed = onBackPressed
                )
            }
        }

        animatedComposable(route = shizukuSettingsRoute) {
            ShizukuSettingsRoute(
                navController = navController, onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = featureFlagSettingsRoute) {
            FeatureFlagSettingsRoute(
                navController = navController, onBackPressed = onBackPressed
            )
        }

        animatedArgumentRouteComposable(route = experimentSettingsRoute) { _, _ ->
            if (uiOverhaul) {
                NewExperimentsSettingsRoute(onBackPressed = onBackPressed)
            } else {
                ExperimentsSettingsRoute(
                    navController = navController, onBackPressed = onBackPressed
                )
            }
        }

        animatedComposable(route = exportImportSettingsRoute) {
            ExportImportSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = debugSettingsRoute) {
            if (uiOverhaul) {
                NewDebugSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
            } else {
                DebugSettingsRoute(
                    navController = navController, onBackPressed = onBackPressed
                )
            }
        }

        animatedComposable(route = logViewerSettingsRoute) {
            if (uiOverhaul) {
                NewLogSettingsRoute(onBackPressed = onBackPressed, navigate = navigate, navController = navController)
            } else {
                LogSettingsRoute(
                    navController = navController, onBackPressed = onBackPressed
                )
            }
        }

        animatedComposable(route = loadDumpedPreferences) {
            LoadDumpedPreferences(onBackPressed = onBackPressed)
        }

        animatedArgumentRouteComposable(route = logTextViewerSettingsRoute) { _, _ ->
            val flag = true
            if (uiOverhaul && flag) {
                NewLogTextSettingsRoute(onBackPressed = onBackPressed)
            } else {
                LogTextSettingsRoute(uiOverhaul = uiOverhaul, onBackPressed = onBackPressed)
            }
        }

        animatedComposable(route = aboutSettingsRoute) {
            if (uiOverhaul) {
                NewAboutSettingsRoute(
                    navigate = navigate, onBackPressed = onBackPressed
                )
            } else {
                AboutSettingsRoute(
                    navController = navController, onBackPressed = onBackPressed
                )
            }
        }

        animatedComposable(route = donateSettingsRoute) {
            DonateSettingsRoute(
                onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = creditsSettingsRoute) {
            if (uiOverhaul) {
                NewCreditsSettingsRoute(onBackPressed = onBackPressed)
            } else {
                CreditsSettingsRoute(onBackPressed = onBackPressed)
            }
        }

        animatedComposable(route = preferredBrowserSettingsRoute) {
            PreferredBrowserSettingsRoute(
                navController = navController, onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = whitelistedBrowsersSettingsRoute) {
            WhitelistedBrowsersSettingsRoute(navController = navController)
        }

        animatedComposable(route = inAppBrowserSettingsRoute) {
            if (uiOverhaul) {
                NewInAppBrowserSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
            } else {
                InAppBrowserSettingsRoute(
                    navController = navController, onBackPressed = onBackPressed
                )
            }
        }

        animatedComposable(route = inAppBrowserSettingsDisableInSelectedRoute) {
            if (uiOverhaul) {
                NewInAppBrowserSettingsDisableInSelectedRoute(onBackPressed = onBackPressed)
            } else {
                InAppBrowserSettingsDisableInSelectedRoute(navController = navController)
            }
        }

        animatedComposable(route = preferredAppsSettingsRoute) {
            PreferredAppSettingsRoute(onBackPressed = onBackPressed)
        }

        if (AndroidVersion.AT_LEAST_API_31_S) {
            animatedComposable(route = appsWhichCanOpenLinksSettingsRoute) {
                if (uiOverhaul) {
                    VerifiedLinkHandlersRoute(onBackPressed = onBackPressed)
                } else {
                    AppsWhichCanOpenLinksSettingsRoute(onBackPressed = onBackPressed)
                }
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

        animatedComposable(route = Routes.Help) {
//            DevBottomSheetSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = Routes.Shortcuts) {
            ShortcutsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = Routes.Updates) {
//            DevBottomSheetSettingsRoute(onBackPressed = onBackPressed)
        }
    }
}
