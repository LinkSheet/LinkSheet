package fe.linksheet.composable.page.mdviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jeziellago.compose.markdowntext.MarkdownText
import fe.android.span.helper.LocalLinkAnnotationStyle
import fe.composekit.component.ContentType
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.page.SaneSettingsScaffold
import fe.linksheet.R
import fe.linksheet.composable.component.appbar.SaneSmallTopAppBar
import fe.linksheet.composable.ui.PreviewTheme
import fe.linksheet.composable.util.debugBorder
import fe.linksheet.module.debug.LocalUiDebug
import fe.linksheet.module.viewmodel.MarkdownViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownViewerWrapper(
    title: String,
    url: String,
    rawUrl: String = url,
    onBackPressed: () -> Unit,
    viewModel: MarkdownViewModel = koinViewModel(),
) {
    var markdown by remember(rawUrl) { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = rawUrl) {
        markdown = viewModel.fetch(rawUrl)
    }

    val handler = LocalUriHandler.current
    MarkdownViewer(
        title = title,
        markdown = markdown,
        onBackPressed = onBackPressed,
        onOpenExternally = {
            handler.openUri(url)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarkdownViewer(
    title: String,
    markdown: String? = null,
    onBackPressed: () -> Unit,
    onOpenExternally: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { false }
    )

    val debug by LocalUiDebug.current.drawBorders.collectAsStateWithLifecycle()
    SaneSettingsScaffold(
        topBar = {
            SaneSmallTopAppBar(
                headline = title,
                enableBackButton = true,
                onBackPressed = onBackPressed,
                actions = {
                    IconButton(
                        modifier = Modifier.debugBorder(debug, 1.dp, Color.Red),
                        onClick = onOpenExternally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                            contentDescription = stringResource(R.string.generic__button_open_external),
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (markdown == null) {
            Box(modifier = Modifier.padding(padding)) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .align(Alignment.Center)
                )
            }
        }

        SaneLazyColumnLayout(padding = padding) {
            item(key = 1, contentType = ContentType.TextItem) {
                if (markdown != null) {
                    MarkdownText(
                        modifier = Modifier,
                        markdown = markdown,
                        linkColor = LocalLinkAnnotationStyle.current.style.color,
                        style = MaterialTheme.typography.bodyMedium,
                        isTextSelectable = true
                    )
                }
            }
        }
    }
}


private class MarkdownPreviewParameterProvider : PreviewParameterProvider<Pair<String, String>> {
    override val values = sequenceOf(
        "Experiments" to """
        ## Experiment default changelog
    
        ### 2024-12-16
    
        * `Improved intent resolver` is updated to `true` on existing installs
        * `Ignore accidental taps while sheet is animating` is updated to `true` on existing installs
    
        Please use this [discussion thread](https://github.com/orgs/LinkSheet/discussions/501) to ask questions and/or report issues/bugs
        """.trimIndent(),
        "Privacy" to """
        LinkSheet does not track the user in any way. LinkSheet always uses common browser headers and never
        includes device information when sending web-requests, but due to the nature of how the internet works, your
        public IP address is not hidden.
        
        ## Features connecting to the internet locally
        
        ### Follow redirects
        
        When the "Follow redirects" feature is enabled, LinkSheet attempts to follow redirects before
        showing the app chooser by sending a `HEAD` request to the opened URL (which means a connection to
        the website is always made, regardless of whether the user actually opens the URL in any app
        afterwards).
        
        Redirects will only be followed for darknet urls (.i2p, .onion) if the corresponding option "Allow darknets" is enabled.
        
        ### Amp2Html
        
        When "Enable Amp2Html" is enabled, LinkSheet will send a `GET` request to the opened URL (again, a
        connection is always made, even if the user does not actually open the URL in any app afterwards) to
        attempt to obtain the non-AMP version of the page. If the page is not an AMP page, or if no non-AMP
        version could be found, the original URL will be opened when the user clicks an app in the bottom
        sheet. 
        
        Amp2Html will only run on darknet urls (.i2p, .onion) if the corresponding option "Allow darknets" is enabled.
        
        ### Downloader
        
        When the "Enable downloader" feature is enabled, LinkSheet will send a `HEAD` request to the opened
        URL (again, a connection is always made, even if the user does not actually open the URL in any app
        afterwards) and check if the `Content-Type` header of the response is not `text/html`.
        
        Enabling "Use mime type from URL" will still send a request if no mime type could be read from the
        opened URL
        
        ## External services
        
        Both "Follow redirects" and "Amp2Html" are also available as external services, meaning that,
        when the corresponding option is enabled ("Follow redirects via external service" and
        "Attempt to obtain non-AMP page version via external service" respectively),
        a [Supabase edge-function](https://github.com/1fexd/linksheet-supabase-functions/) is used to follow
        redirects or convert an AMP page to the normal HTML page. The functions only cache the timestamp,
        input and output link. The cache will periodically be exported from the database directly into LinkSheet,
        so common links can easily be resolved locally.
        
        When "Follow redirects" and "Follow redirects via external service" are enabled, only
        links [known to be trackers (checkout the *.txt files)](https://github.com/1fexd/fastforward-ext/releases/latest)
        will be sent to the edge function.
        
        If "Enable Amp2Html" and "Attempt to obtain non-AMP page version via external service", ALL links
        are sent to the edge function.
        
        Darknet links (.i2p, .onion) are NEVER sent to this external service.
        
        ## Logs
        
        ### Crash log viewer
        
        The crash log viewer displays exceptions which could not be handled by the application. Usually,
        exception messages do not contain any personal data and can safely be shared with others.
        
        ### Internal log viewer
        
        The internal log viewer (available from version `0.0.32` onwards) will default to exporting a
        redacted version of the log. In the redacted version, personal identifiers like package names or
        hosts are hashed with `HmacSHA256` (a random key is generated when the app is launched for the first
        time). This approach ensures privacy while still allowing debugging.
        
        ### Other log content
        
        The log will contain the LinkSheet version.
        
        Additionally, both logs will include the device fingerprint by default. This fingerprint
        contains the
        device brand, device
        name, Android version, Android build id, Android version incremental id, user type (e.g. user) and
        tags (e.g. release-keys). While this fingerprint may be considered personal data, it may be
        incredibly useful while debugging issues.
        
        Settings are also included in both logs by default since they may also help a lot while debugging
        issues. While they do not include any personal data (settings containing package names are obscured
        when the log is exported in redacted mode), exporting them can still be disabled.
        
        ## ADB logcat
        
        ADB logcats of LinkSheet may include personal identifiable information like installed packages or
        visited hosts as well as preferences. Logcats should only be shared in rare cases where a full log
        is required to debug an issue. Logcats should not be published publicly.
        
        ## Exports
        
        When exporting LinkSheet settings, you can select to export the log key. Please make sure not to share exports containing log keys with other people as the key may be sensitive information.
        """.trimIndent()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(group = "MarkdownViewerPage", showBackground = false)
@Composable
private fun PreviewText(
    @PreviewParameter(MarkdownPreviewParameterProvider::class) preview: Pair<String, String>,
) {
    val (title, markdown) = preview

    PreviewTheme {
        MarkdownViewer(
            title = title,
            markdown = markdown,
            onBackPressed = {},
            onOpenExternally = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(group = "MarkdownViewerPage", showBackground = false)
@Composable
private fun PreviewLoading() {
    PreviewTheme {
        MarkdownViewer(
            title = "Experiments",
            markdown = null,
            onBackPressed = {},
            onOpenExternally = {}
        )
    }
}
