package fe.linksheet.experiment.ui.overhaul.composable.page.settings.misc

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.module.viewmodel.GeneralSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun MiscSettingsRoute(onBackPressed: () -> Unit, viewModel: GeneralSettingsViewModel = koinViewModel(), ) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.misc_settings), onBackPressed = onBackPressed) {
        item(key = R.string.always_show_package_name, contentType = ContentTypeDefaults.SingleGroupItem) {
            PreferenceSwitchListItem(
                preference = viewModel.alwaysShowPackageName,
                headlineContent = textContent(R.string.always_show_package_name),
                supportingContent = textContent(R.string.always_show_package_name_explainer),
            )
        }
    }
}
