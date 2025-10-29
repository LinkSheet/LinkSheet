package fe.linksheet.composable.page.settings.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.toEnabledContentSet
import fe.linksheet.R
import fe.linksheet.composable.component.list.item.type.PreferenceRadioButtonListItem
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.composable.ui.ThemeV2
import fe.composekit.core.AndroidVersion
import fe.composekit.preference.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel


private val themes = arrayOf(ThemeV2.Light, ThemeV2.Dark, ThemeV2.System)

@Composable
fun ThemeSettingsRoute(onBackPressed: () -> Unit, viewModel: ThemeSettingsViewModel = koinViewModel()) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.theme), onBackPressed = onBackPressed) {
        if (AndroidVersion.isAtLeastApi31S()) {
            item(key = R.string.theme_enable_material_you, contentType = ContentType.SingleGroupItem) {
                PreferenceSwitchListItem(
                    statePreference = viewModel.themeMaterialYou,
                    headlineContent = textContent(R.string.theme_enable_material_you),
                    supportingContent = textContent(R.string.theme_enable_material_you_explainer)
                )
            }

            divider(id =  R.string.theme_mode)
        }

        group(size = 4) {
            items(array = themes) { item, padding, shape ->
                PreferenceRadioButtonListItem(
                    shape = shape,
                    padding = padding,
                    value = item,
                    statePreference = viewModel.themeV2,
                    position = ContentPosition.Trailing,
                    headlineContent = textContent(item.id)
                )
            }

            item(key = R.string.theme_enable_amoled) { padding, shape ->
                val themeV2 by viewModel.themeV2.collectAsStateWithLifecycle()

                PreferenceSwitchListItem(
                    enabled = (themeV2 == ThemeV2.System || themeV2 == ThemeV2.Dark).toEnabledContentSet(),
                    shape = shape,
                    padding = padding,
                    statePreference = viewModel.themeAmoled,
                    headlineContent = textContent(R.string.theme_enable_amoled),
                    supportingContent = textContent(R.string.theme_enable_amoled_explainer)
                )
            }
        }
    }
}
