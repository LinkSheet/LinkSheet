package app.linksheet.compose.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import fe.android.compose.content.rememberOptionalContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaneSmallTopAppBar(
    headline: String?,
    enableBackButton: Boolean,
    onBackPressed: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior?,
) {
    val title = rememberOptionalContent(headline) {
        SaneAppBarTitle(headline = it)
    }

    val navigationIcon = rememberOptionalContent(enableBackButton) {
        SaneAppBarBackButton(onBackPressed = onBackPressed)
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = title ?: {},
        navigationIcon = navigationIcon ?: {},
        scrollBehavior = scrollBehavior,
        actions = actions,
    )
}
