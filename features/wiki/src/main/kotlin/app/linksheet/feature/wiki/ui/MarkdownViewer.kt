package app.linksheet.feature.wiki.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import app.linksheet.compose.appbar.SaneSmallTopAppBar
import app.linksheet.compose.debug.LocalUiDebug
import app.linksheet.compose.debugBorder
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.wiki.viewmodel.MarkdownViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import fe.android.span.helper.LocalLinkAnnotationStyle
import fe.composekit.component.CommonDefaults
import fe.composekit.component.ContentType
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.composekit.component.page.SaneSettingsScaffold
import org.koin.androidx.compose.koinViewModel
import app.linksheet.compose.R as CommonR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownViewerWrapper(
    onBackPressed: () -> Unit,
    viewModel: MarkdownViewModel = koinViewModel(),
) {
    val markdown by viewModel.markdownText.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val handler = LocalUriHandler.current

    LaunchedEffect(key1 = Unit) {
        viewModel.init()
    }

    MarkdownViewer(
        title = viewModel.data.customTitle?.let { stringResource(id = it) } ?: viewModel.data.title,
        markdown = markdown,
        isLoading = isLoading,
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        onBackPressed = onBackPressed,
        onOpenExternally = {
            handler.openUri(viewModel.data.url)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarkdownViewer(
    title: String,
    markdown: String? = null,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBackPressed: () -> Unit,
    onOpenExternally: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { false }
    )

    val state = rememberPullToRefreshState()

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
                            contentDescription = stringResource(CommonR.string.generic__button_text_open_external),
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.padding(padding)) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .align(Alignment.Center)
                )
            }
        }

        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = state,
            indicator = {
                @OptIn(ExperimentalMaterial3ExpressiveApi::class)
                PullToRefreshDefaults.LoadingIndicator(
                    state = state,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        ) {
            SaneLazyColumnLayout(padding = CommonDefaults.EmptyPadding) {
                item(key = 1, contentType = ContentType.TextItem) {
                    if (markdown != null) {
                        MarkdownText(
                            modifier = Modifier,
                            markdown = markdown,
                            linkColor = LocalLinkAnnotationStyle.current.style.color,
                            syntaxHighlightColor = MaterialTheme.colorScheme.tertiary,
                            syntaxHighlightTextColor = MaterialTheme.colorScheme.onTertiary,
                            style = MaterialTheme.typography.bodyMedium,
                            isTextSelectable = true,
//                        onLinkClicked = {
//                            Log.d("MarkdownViewer", "$it")
//                        }
                        )
                    }
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
        ### Amp2Html
        
        When "Enable Amp2Html" is enabled, LinkSheet will send a `GET` request to the opened URL (again, a
        connectioture is enabled, LinkSheet will send a `HEAD` request to the opened
        
        ## External services
        
        Both "Follow redirects" and "Amp2Html" are also available as external services, meaning that""".trimIndent()
    )
}


@Composable
private fun MarkdownViewerPreviewBase(preview: Pair<String, String>) {
    val (title, markdown) = preview

    PreviewTheme {
        MarkdownViewer(
            title = title,
            markdown = markdown,
            isLoading = false,
            isRefreshing = false,
            onRefresh = {},
            onBackPressed = {},
            onOpenExternally = {}
        )
    }
}

@Preview(
    group = "MarkdownViewerPage",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun PreviewTextLight(
    @PreviewParameter(MarkdownPreviewParameterProvider::class) preview: Pair<String, String>,
) {
    MarkdownViewerPreviewBase(preview)
}

@Preview(
    group = "MarkdownViewerPage",
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewTextDark(
    @PreviewParameter(MarkdownPreviewParameterProvider::class) preview: Pair<String, String>,
) {
    MarkdownViewerPreviewBase(preview)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(group = "MarkdownViewerPage", showBackground = false)
@Composable
private fun PreviewLoading() {
    PreviewTheme {
        MarkdownViewer(
            title = "Experiments",
            markdown = null,
            isLoading = false,
            isRefreshing = false,
            onRefresh = {},
            onBackPressed = {},
            onOpenExternally = {}
        )
    }
}
