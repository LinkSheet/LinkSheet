package fe.linksheet.composable.settings.theme

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.preference.PreferenceRadioButtonListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.SwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.preference.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.GroupValueProvider
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.ui.Theme
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.compose.koinViewModel


@Stable
data class ThemeSettingsData(val theme: Theme, @StringRes val textId: Int) : GroupValueProvider<Int> {
    override val key: Int = textId

    companion object {
        val data = arrayOf(
            ThemeSettingsData(Theme.Light, R.string.light),
            ThemeSettingsData(Theme.Dark, R.string.dark),
            ThemeSettingsData(Theme.System, R.string.system)
        )
    }
}

@Composable
fun ThemeSettingsRoute(onBackPressed: () -> Unit, viewModel: ThemeSettingsViewModel = koinViewModel()) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.theme), onBackPressed = onBackPressed) {
        if (AndroidVersion.AT_LEAST_API_31_S) {
            item(key = R.string.theme_enable_material_you, contentType = "single") {
                SwitchListItem(
                    shape = ShapeListItemDefaults.SingleShape,
                    checked = viewModel.themeMaterialYou(),
                    onCheckedChange = { viewModel.themeMaterialYou(it) },
                    headlineContentText = stringResource(id = R.string.theme_enable_material_you),
                    supportingContentText = stringResource(id = R.string.theme_enable_material_you_explainer)
                )
            }

            divider(stringRes = R.string.theme_mode)
        }

        group(size = 4) {
            items(values = ThemeSettingsData.data) { item, padding, shape ->
                PreferenceRadioButtonListItem(
                    shape = shape,
                    padding = padding,
                    value = item.theme,
                    preference = viewModel.theme,
                    headlineContentTextId = item.textId
                )
            }

            item(key = R.string.theme_enable_amoled) { padding, shape ->
                PreferenceSwitchListItem(
                    enabled = viewModel.theme() == Theme.System || viewModel.theme() == Theme.Dark,
                    shape = shape,
                    padding = padding,
                    preference = viewModel.themeAmoled,
                    headlineContentTextId = R.string.theme_enable_amoled,
                    supportingContentTextId = R.string.theme_enable_amoled_explainer
                )
            }
        }
    }
}
