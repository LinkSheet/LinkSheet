package fe.linksheet.composable.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.junkfood.seal.ui.component.BackButton
import fe.linksheet.ui.theme.HkGroteskFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScaffold(@StringRes headline: Int, onBackPressed: () -> Unit,  content: @Composable (PaddingValues) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent),
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = headline),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = { padding -> content(padding) }
    )
}