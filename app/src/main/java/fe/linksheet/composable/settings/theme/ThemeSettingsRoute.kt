package fe.linksheet.composable.settings.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceRadioButtonListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.ui.ThemeV2
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.compose.koinViewModel


private val themes = arrayOf(ThemeV2.Light, ThemeV2.Dark, ThemeV2.System)

@Composable
fun ThemeSettingsRoute(onBackPressed: () -> Unit, viewModel: ThemeSettingsViewModel = koinViewModel()) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.theme), onBackPressed = onBackPressed) {
        if (AndroidVersion.AT_LEAST_API_31_S) {
            item(key = R.string.theme_enable_material_you, contentType = ContentTypeDefaults.SingleGroupItem) {
                PreferenceSwitchListItem(
                    preference = viewModel.themeMaterialYou,
                    headlineContent = textContent(R.string.theme_enable_material_you),
                    supportingContent = textContent(R.string.theme_enable_material_you_explainer)
                )
            }

            divider(stringRes = R.string.theme_mode)
        }

        group(size = 4) {
            items(array = themes) { item, padding, shape ->
                PreferenceRadioButtonListItem(
                    shape = shape,
                    padding = padding,
                    value = item,
                    preference = viewModel.themeV2,
                    position = ContentPosition.Trailing,
                    headlineContent = textContent(item.id)
                )
            }

            item(key = R.string.theme_enable_amoled) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = viewModel.themeV2() == ThemeV2.System || viewModel.themeV2() == ThemeV2.Dark,
                    shape = shape,
                    padding = padding,
                    preference = viewModel.themeAmoled,
                    headlineContent = textContent(R.string.theme_enable_amoled),
                    supportingContent = textContent(R.string.theme_enable_amoled_explainer)
                )
            }
        }
    }
}
