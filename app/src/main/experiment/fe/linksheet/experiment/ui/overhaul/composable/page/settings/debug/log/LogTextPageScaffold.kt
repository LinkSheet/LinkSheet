package fe.linksheet.experiment.ui.overhaul.composable.page.settings.debug.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import fe.linksheet.component.page.SaneSettingsScaffold
import fe.linksheet.component.page.layout.SaneLazyColumnPageDefaults
import fe.linksheet.component.page.layout.SaneLazyColumnPageLayout
import fe.linksheet.component.page.layout.SaneLazyListScope
import fe.linksheet.experiment.ui.overhaul.composable.component.appbar.SaneLargeTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogTextPageScaffold(
    modifier: Modifier = Modifier,
    headline: String,
    onBackPressed: () -> Unit = {},
    enableBackButton: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: SaneLazyListScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    SaneSettingsScaffold(
        modifier = modifier.then(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)),
        topBar = {
            SaneLargeTopAppBar(
                headline = headline,
                enableBackButton = enableBackButton,
                onBackPressed = onBackPressed,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        content = { padding ->
            SaneLazyColumnPageLayout(
                padding = padding,
                contentPadding = PaddingValues(
                    top = SaneLazyColumnPageDefaults.VerticalSpacing,
                    bottom = SaneLazyColumnPageDefaults.BottomSpacing
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                content = content
            )
        }
    )
}
