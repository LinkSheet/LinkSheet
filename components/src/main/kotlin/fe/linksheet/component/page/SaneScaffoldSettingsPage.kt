package fe.linksheet.component.page

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fe.linksheet.component.page.layout.SaneLazyColumnPageLayout
import fe.linksheet.component.page.layout.SaneLazyListScope

@Composable
fun SaneScaffoldSettingsPageInternal(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: SaneLazyListScope.() -> Unit,
) {
    SaneSettingsScaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        content = { padding -> SaneLazyColumnPageLayout(padding = padding, content = content) }
    )
}

@Composable
fun SaneSettingsScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
}
