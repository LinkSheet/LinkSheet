package fe.linksheet.experiment.ui.overhaul.composable.page.settings.misc

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.GeneralSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun MiscSettingsRoute(onBackPressed: () -> Unit, viewModel: GeneralSettingsViewModel = koinViewModel()) {
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.misc_settings), onBackPressed = onBackPressed) {
        item(key = R.string.always_show_package_name, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                preference = viewModel.alwaysShowPackageName,
                headlineContent = textContent(R.string.always_show_package_name),
                supportingContent = textContent(R.string.always_show_package_name_explainer),
            )
        }
    }
}
