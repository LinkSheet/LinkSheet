package fe.linksheet.experiment.ui.overhaul.composable.page.settings.misc

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.preference.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.GeneralSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun MiscSettingsRoute(onBackPressed: () -> Unit, viewModel: GeneralSettingsViewModel = koinViewModel(), ) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.misc_settings), onBackPressed = onBackPressed) {
        item(key = R.string.always_show_package_name, contentType = ContentTypeDefaults.SingleGroupItem) {
            PreferenceSwitchListItem(
                preference = viewModel.alwaysShowPackageName,
                headlineContentTextId = R.string.always_show_package_name,
                supportingContentTextId = R.string.always_show_package_name_explainer
            )
        }
    }
}
