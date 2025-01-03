package fe.linksheet.activity.bottomsheet.content.success.url

import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import fe.android.compose.icon.BitmapIconPainter.Companion.bitmap
import fe.android.compose.icon.IconPainter
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.android.compose.version.AndroidVersion
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.module.resolver.ImprovedIntentResolver
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.activity.bottomsheet.BottomSheetStateController
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.intent.Intents
import fe.linksheet.module.profile.CrossProfile
import fe.linksheet.module.profile.ProfileSwitcher
import fe.linksheet.module.resolver.LibRedirectResult
import me.saket.unfurl.UnfurlResult

@Composable
fun UrlBarWrapper(
    result: IntentResolveResult.Default,
    profileSwitcher: ProfileSwitcher,
    enableIgnoreLibRedirectButton: Boolean,
    enableSwitchProfile: Boolean,
    enableUrlCopiedToast: Boolean,
    enableDownloadStartedToast: Boolean,
    enableUrlCardDoubleTap: Boolean,
    enableManualRedirect: Boolean,
    hideAfterCopying: Boolean,
    controller: BottomSheetStateController,
    showToast: (Int) -> Unit,
    copyUrl: (String, String) -> Unit,
    startDownload: (String, DownloadCheckResult.Downloadable) -> Unit,
    launchApp: (ActivityAppInfo, Intent, ClickModifier) -> Unit,
) {
    val uriString = result.uri.toString()
    val clipboardLabel = stringResource(id = R.string.generic__text_url)
    val activity = LocalActivity.current

    UrlBar(
        uri = uriString,
        profiles = AndroidVersion.atLeastApi(Build.VERSION_CODES.R) {
            if (enableSwitchProfile) profileSwitcher.getProfiles() else null
        },
        switchProfile = AndroidVersion.atLeastApi(Build.VERSION_CODES.R) {
            { crossProfile, url ->
                controller.hideAndFinish()
                profileSwitcher.switchTo(crossProfile, url, activity)
            }
        },
        unfurlResult = result.unfurlResult,
        downloadable = result.downloadable,
        libRedirected = if (enableIgnoreLibRedirectButton) result.libRedirectResult as? LibRedirectResult.Redirected else null,
        copyUri = { uri ->
            copyUrl(clipboardLabel, uri)

            if (enableUrlCopiedToast) {
                showToast(R.string.url_copied)
            }

            if (hideAfterCopying) {
                controller.hideAndFinish()
            }
        },
        shareUri = { uri ->
            controller.hideAndFinish()
            controller.startActivity(controller.createChooser(Intents.createShareUriIntent(uri)))
        },
        downloadUri = { uri, downloadResult ->
            startDownload(uri, downloadResult)

            if (enableDownloadStartedToast) {
                showToast(R.string.download_started)
            }

            if (hideAfterCopying) {
                controller.hideAndFinish()
            }
        },
        ignoreLibRedirect = { redirectedResult ->
            controller.onNewIntent(
                Intents.createSelfIntent(
                    redirectedResult.originalUri,
                    bundleOf(LibRedirectDefault.libRedirectIgnore to true)
                )
            )
        },
        manualRedirect = if (enableManualRedirect) { uri ->
            controller.onNewIntent(
                Intents.createSelfIntent(
                    uri.toUri(),
                    bundleOf(ImprovedIntentResolver.IntentKeyResolveRedirects to true)
                )
            )
        } else null,
        onDoubleClick = {
            if (result.app != null) {
                launchApp(result.app, result.intent, ClickModifier.None)
            }

            Unit
        }.takeIf { enableUrlCardDoubleTap }
    )
}


@Composable
fun UrlBar(
    uri: String,
    unfurlResult: UnfurlResult?,
    profiles: List<CrossProfile>?,
    downloadable: DownloadCheckResult,
    libRedirected: LibRedirectResult.Redirected?,
    copyUri: (String) -> Unit,
    shareUri: (String) -> Unit,
    switchProfile: ((CrossProfile, String) -> Unit)?,
    downloadUri: ((String, DownloadCheckResult.Downloadable) -> Unit)? = null,
    ignoreLibRedirect: ((LibRedirectResult.Redirected) -> Unit)? = null,
    manualRedirect: ((String) -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
    ) {
        UrlCard(uri = uri, unfurlResult = unfurlResult, onDoubleClick = onDoubleClick)

        Spacer(modifier = Modifier.height(5.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            item {
                UrlActionButton(
                    text = textContent(R.string.copy_url),
                    icon = Icons.Filled.ContentCopy.iconPainter,
                    onClick = { copyUri(uri) }
                )
            }

            item {
                UrlActionButton(
                    text = textContent(R.string.share),
                    icon = Icons.Filled.Share.iconPainter,
                    onClick = { shareUri(uri) }
                )
            }

            if (downloadable.isDownloadable()) {
                item {
                    UrlActionButton(
                        text = textContent(R.string.download),
                        icon = Icons.Filled.Download.iconPainter,
                        onClick = { downloadUri!!(uri, downloadable as DownloadCheckResult.Downloadable) }
                    )
                }
            }

            if (libRedirected != null) {
                item {
                    UrlActionButton(
                        text = textContent(R.string.ignore_libredirect),
                        icon = Icons.Filled.FastForward.iconPainter,
                        onClick = { ignoreLibRedirect!!(libRedirected) }
                    )
                }
            }

            if (manualRedirect != null) {
                item {
                    UrlActionButton(
                        text = textContent(R.string.bottom_sheet__button_manual_redirect),
                        icon = Icons.Rounded.DoubleArrow.iconPainter,
                        onClick = { manualRedirect(uri) }
                    )
                }
            }

            if (switchProfile != null && profiles != null) {
                for (target in profiles) {
                    item {
                        UrlActionButton(
                            text = text(target.switchLabel),
                            icon = bitmap(target.bitmap),
                            onClick = { switchProfile(target, uri) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.25f))

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun UrlActionButton(text: TextContent, icon: IconPainter, onClick: () -> Unit) {
    val painter = icon.rememberPainter()

    ElevatedAssistChip(
        onClick = onClick,
        elevation = AssistChipDefaults.assistChipElevation(),
        shape = CircleShape,
        leadingIcon = {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painter,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        },
        label = text.content
    )
}

@Preview(name = "UrlPreview", showBackground = true)
@Composable
private fun UrlBarPreview() {
    val clipboardManager = LocalContext.current.getSystemService<ClipboardManager>()!!


//    UrlBar(
//        uri = uri,
//        unfurlResult = unfurled,
//        downloadable = false,
//        libRedirected = false,
//        copyUri = { /*TODO*/ },
//        shareUri = { /*TODO*/ }
//    )

//    UrlBar(
//        uri = Uri.parse("https://developer.android.com/jetpack/compose/text/configure-layout"),
//        clipboardManager = clipboardManager,
//        urlCopiedToast = MockRepositoryState.preference(true),
//        hideAfterCopying = MockRepositoryState.preference(true),
//        showToast = {},
//        hideDrawer = {},
//        shareUri = {}
//    )
}
