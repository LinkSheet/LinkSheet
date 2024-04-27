package fe.linksheet.experiment.ui.overhaul.composable.component.page

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyColumnPageLayout
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyListScope
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun SaneScaffoldSettingsPage(
    modifier: Modifier = Modifier,
    headline: String,
    onBackPressed: () -> Unit,
    enableBackButton: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: SaneLazyListScope.() -> Unit,
) {
    SaneSettingsScaffold(
        modifier = modifier,
        headline = headline,
        onBackPressed = onBackPressed,
        enableBackButton = enableBackButton,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        content = { padding -> SaneLazyColumnPageLayout(padding = padding, content = content) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaneSettingsScaffold(
    modifier: Modifier = Modifier,
    headline: String,
    enableBackButton: Boolean = true,
    onBackPressed: () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent),
                title = {
                    Text(
                        modifier = Modifier,
                        text = headline,
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    if (enableBackButton) {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
}
