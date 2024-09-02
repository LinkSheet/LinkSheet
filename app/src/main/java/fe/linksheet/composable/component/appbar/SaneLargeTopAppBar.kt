package fe.linksheet.composable.component.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import fe.linksheet.R
import fe.linksheet.ui.HkGroteskFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaneLargeTopAppBar(
    headline: String,
    enableBackButton: Boolean,
    onBackPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
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
}
