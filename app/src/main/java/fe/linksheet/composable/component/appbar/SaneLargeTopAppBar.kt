package fe.linksheet.composable.component.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fe.android.compose.content.rememberOptionalContent
import fe.linksheet.composable.util.debugBorder
import fe.linksheet.module.debug.LocalUiDebug

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaneLargeTopAppBar(
    headline: String,
    enableBackButton: Boolean,
    onBackPressed: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val debug by LocalUiDebug.current.drawBorders.collectAsStateWithLifecycle()
    val navigationIcon = rememberOptionalContent(enableBackButton) {
        SaneAppBarBackButton(
            modifier = Modifier.debugBorder(debug, 1.dp, Color.Cyan),
            onBackPressed = onBackPressed
        )
    }

    LargeTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = Color.Unspecified,
            actionIconContentColor = Color.Unspecified
        ),
        title = {
            SaneAppBarTitle(
                modifier = Modifier.debugBorder(debug, 1.dp, Color.Green),
                headline = headline
            )
        },
        actions = actions,
        navigationIcon = navigationIcon ?: {},
        scrollBehavior = scrollBehavior
    )
}
