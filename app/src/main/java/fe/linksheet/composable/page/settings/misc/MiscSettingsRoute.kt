package fe.linksheet.composable.page.settings.misc

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.layout.column.group
import fe.linksheet.R
import fe.linksheet.composable.component.list.item.type.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.composable.component.page.twoline.SwitchPreferenceItemNew
import fe.linksheet.module.viewmodel.GeneralSettingsViewModel
import org.koin.androidx.compose.koinViewModel


private object MiscSettingsRouteData {
    fun init(vm: GeneralSettingsViewModel): List<SwitchPreferenceItemNew> {
        return listOf(
            SwitchPreferenceItemNew(
                vm.alwaysShowPackageName,
                textContent(R.string.always_show_package_name),
                textContent(R.string.always_show_package_name_explainer),
            ),
            SwitchPreferenceItemNew(
                vm.homeClipboardCard,
                textContent(R.string.settings_home__title_clipboard_card),
                textContent(R.string.settings_home__text_clipboard_card),
            )
        )
    }
}

@Composable
fun MiscSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: GeneralSettingsViewModel = koinViewModel(),
    data: List<SwitchPreferenceItemNew> = MiscSettingsRouteData.init(viewModel)
) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.misc_settings), onBackPressed = onBackPressed) {
        group(list = data) { data, padding, shape ->
            PreferenceSwitchListItem(
                shape = shape,
                padding = padding,
                statePreference = data.preference,
                headlineContent = data.headlineContent,
                supportingContent = data.subtitleContent,
            )
        }
    }
}
