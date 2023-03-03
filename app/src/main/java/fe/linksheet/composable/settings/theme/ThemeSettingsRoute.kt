package fe.linksheet.composable.settings.theme

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.ui.theme.Theme


@Composable
fun ThemeSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel
) {
    val themes = remember {
        mutableStateListOf(
            ThemeHolder(Theme.System, R.string.system),
            ThemeHolder(Theme.Light, R.string.light),
            ThemeHolder(Theme.Dark, R.string.dark),
            ThemeHolder(Theme.AmoledBlack, R.string.amoled_black)
        )
    }

    SettingsScaffold(R.string.theme, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            themes.forEach { holder ->
                item(key = holder.key) {
                    RadioButtonRow(
                        onClick = { viewModel.onThemeChange(holder.theme) },
                        onLongClick = null,
                        selected = viewModel.theme == holder.theme
                    ) {
                        Texts(headline = holder.headline, subtitle = null)
                    }
                }
            }
        }
    }
}

data class ThemeHolder(
    val theme: Theme,
    @StringRes val headline: Int,
) {
    val key = theme.name.lowercase()
}