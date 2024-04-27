package fe.linksheet.experiment.ui.overhaul.composable.page.settings.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.group
import fe.linksheet.experiment.ui.overhaul.composable.component.page.twoline.SwitchPreferenceItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.twoline.rememberTwoLinePreferenceGroup
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent
import fe.linksheet.module.viewmodel.NotificationSettingsViewModel
import org.koin.androidx.compose.koinViewModel

private object NewNotificationSettingsRouteData {
    fun init(vm: NotificationSettingsViewModel): List<SwitchPreferenceItem> {
        return listOf(
            SwitchPreferenceItem(
                vm.urlCopiedToast,
                R.string.url_copied_toast,
                R.string.url_copied_toast_explainer
            ),
            SwitchPreferenceItem(
                vm.downloadStartedToast,
                R.string.download_started_toast,
                R.string.download_started_toast_explainer
            ),
            SwitchPreferenceItem(
                vm.openingWithAppToast,
                R.string.opening_with_app_toast,
                R.string.opening_with_app_toast_explainer
            ),
            SwitchPreferenceItem(
                vm.resolveViaToast,
                R.string.resolve_via_toast,
                R.string.resolve_via_toast_explainer
            ),
            SwitchPreferenceItem(
                vm.resolveViaFailedToast,
                R.string.resolve_via_failed_toast,
                R.string.resolve_via_failed_toast_explainer
            )
        )
    }
}

@Composable
fun NewNotificationSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: NotificationSettingsViewModel = koinViewModel(),
) {
    val notificationSettings = rememberTwoLinePreferenceGroup(viewModel) { vm ->
        NewNotificationSettingsRouteData.init(vm)
    }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.notifications), onBackPressed = onBackPressed) {
        group(items = notificationSettings) { data, padding, shape ->
            PreferenceSwitchListItem(
                shape = shape,
                padding = padding,
                preference = data.preference,
                headlineContent = textContent(data.headlineId),
                supportingContent = textContent(data.subtitleId),
            )
        }
    }
}

