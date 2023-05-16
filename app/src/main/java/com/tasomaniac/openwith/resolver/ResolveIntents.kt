package com.tasomaniac.openwith.resolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.tasomaniac.openwith.extension.componentName
import com.tasomaniac.openwith.extension.isSchemeTypicallySupportedByBrowsers
import com.tasomaniac.openwith.preferred.PreferredResolver
import fe.fastforwardkt.FastForwardLoader
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.activity.bottomsheet.BottomSheetViewModel
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.getUri
import fe.linksheet.extension.newIntent
import fe.linksheet.extension.queryResolveInfosByIntent
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.resolver.InAppBrowserHandler
import fe.linksheet.resolver.BottomSheetGrouper
import timber.log.Timber

object ResolveIntents {
    val fastForwardRulesObject by lazy { FastForwardLoader.loadBuiltInFastForwardRules() }
    private val libRedirectServices by lazy { LibRedirectLoader.loadBuiltInServices() }
    private val libRedirectInstances by lazy { LibRedirectLoader.loadBuiltInInstances() }

    suspend fun resolve(
        context: Context,
        intent: Intent,
        referrer: Uri?,
        viewModel: BottomSheetViewModel
    ): BottomSheetResult {
        Timber.tag("ResolveIntents").d("Intent: $intent")

        var uri = intent.getUri(viewModel.useClearUrls, viewModel.useFastForwardRules)
        var followRedirect: BottomSheetViewModel.FollowRedirect? = null

        if (viewModel.followRedirects && uri != null) {
            viewModel.followRedirects(
                uri,
                viewModel.followRedirectsLocalCache,
                fastForwardRulesObject
            ).getOrNull()?.let {
                followRedirect = it
                uri = Uri.parse(it.resolvedUrl)
            }
        }

        if (viewModel.enableLibRedirect) {
            val service = LibRedirect.findServiceForUrl(uri.toString(), libRedirectServices)
            Timber.tag("ResolveIntents").d("LibRedirect $service")
            if (service != null && viewModel.loadLibRedirectState(service.key) == true) {
                val savedDefault = viewModel.getLibRedirectDefault(service.key)
                val redirected = if (savedDefault != null) {
                    val instanceUrl =
                        if (savedDefault.instanceUrl == SettingsViewModel.libRedirectRandomInstanceKey) {
                            libRedirectInstances.find { it.frontendKey == savedDefault.frontendKey }?.hosts?.random()
                                ?: savedDefault.instanceUrl
                        } else savedDefault.instanceUrl

                    LibRedirect.redirect(
                        uri.toString(),
                        savedDefault.frontendKey,
                        instanceUrl
                    )
                } else {
                    val defaultInstance =
                        LibRedirect.getDefaultInstanceForFrontend(service.defaultFrontend.key)
                    LibRedirect.redirect(
                        uri.toString(),
                        service.defaultFrontend.key,
                        defaultInstance?.first()!!
                    )
                }

                Timber.tag("ResolveIntents").d("LibRedirect $redirected")
                if (redirected != null) {
                    uri = Uri.parse(redirected)
                }
            }
        }

        val downloadable = if (viewModel.enableDownloader && uri != null) {
            viewModel.checkIsDownloadable(uri!!)
        } else Downloader.DownloadCheckResult.NonDownloadable

        val preferredApp = uri?.let {
            PreferredResolver.resolve(context, it.host!!)
        }

        Timber.tag("ResolveIntents").d("PreferredApp: $preferredApp")

        val hostHistory = uri?.let {
            PreferredResolver.resolveHostHistory(context, it.host!!)
        } ?: emptyMap()


        Timber.tag("ResolveIntents").d("HostHistory: $hostHistory")
        Timber.tag("ResolveIntents").d("SourceIntent: $intent ${intent.extras}")

        val isCustomTab = intent.hasExtra(CustomTabsIntent.EXTRA_SESSION)
        val allowCustomTab = InAppBrowserHandler.shouldAllowCustomTab(referrer, viewModel)

        val newIntent = intent.newIntent(uri, !isCustomTab || !allowCustomTab)
        if (allowCustomTab) {
            newIntent.extras?.keySet()?.filter { !it.contains("customtabs") }?.forEach { key ->
                Timber.tag("ResolveIntents").d("CustomTab: Remove extra: $key")
                newIntent.removeExtra(key)
            }
        }

        Timber.tag("ResolveIntents").d("NewIntent: $newIntent ${newIntent.extras}")

        val resolvedList = context.packageManager
            .queryResolveInfosByIntent(newIntent, true)
            .toMutableList()

        Timber.tag("ResolveIntents").d("ResolveListPreSort: $resolvedList")

        Timber.tag("ResolveIntents")
            .d("PreferredApp ComponentName: ${preferredApp?.app?.componentName}")

        val browserMode = if (newIntent.isSchemeTypicallySupportedByBrowsers()) {
            BrowserHandler.handleBrowsers(resolvedList, viewModel)
        } else null

        Timber.tag("ResolveIntents").d("BrowserMode: $browserMode")

        val (grouped, filteredItem, showExtended) = BottomSheetGrouper.group(
            context,
            resolvedList,
            hostHistory,
            preferredApp?.app,
            !viewModel.dontShowFilteredItem
        )

        val selectedBrowserIsSingleOption = browserMode?.first == BrowserHandler.BrowserMode.SelectedBrowser
                && resolvedList.singleOrNull()?.activityInfo?.componentName() == browserMode.second?.activityInfo?.componentName()

        val noBrowsersPresentOnlySingleApp = browserMode?.first == BrowserHandler.BrowserMode.None && resolvedList.size == 1


        Timber.tag("ResolveIntents").d(
            "Grouped: $grouped, filteredItem: $filteredItem, showExtended: $showExtended, selectedBrowserIsSingleOption: $selectedBrowserIsSingleOption"
        )

        return BottomSheetResult(
            uri,
            grouped,
            filteredItem,
            showExtended,
            preferredApp?.app?.alwaysPreferred,
            selectedBrowserIsSingleOption || noBrowsersPresentOnlySingleApp,
            followRedirect,
            downloadable
        )
    }
}