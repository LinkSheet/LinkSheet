package fe.linksheet.composable.page.settings.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.layout.column.group
import fe.linksheet.R
import fe.linksheet.composable.component.list.item.type.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.composable.component.page.twoline.SwitchPreferenceItemNew
import fe.linksheet.composable.component.page.twoline.rememberTwoLinePreferenceGroup
import fe.linksheet.module.viewmodel.NotificationSettingsViewModel
import org.koin.androidx.compose.koinViewModel

private object NewNotificationSettingsRouteData {
    fun init(vm: NotificationSettingsViewModel): List<SwitchPreferenceItemNew> {
        return listOf(
            SwitchPreferenceItemNew(
                vm.urlCopiedToast,
                textContent(R.string.url_copied_toast),
                textContent(R.string.url_copied_toast_explainer),
            ),
            SwitchPreferenceItemNew(
                vm.downloadStartedToast,
                textContent(R.string.download_started_toast),
                textContent(R.string.download_started_toast_explainer),
            ),
            SwitchPreferenceItemNew(
                vm.openingWithAppToast,
                textContent(R.string.opening_with_app_toast),
                textContent(R.string.opening_with_app_toast_explainer),
            ),
            SwitchPreferenceItemNew(
                vm.resolveViaToast,
                textContent(R.string.resolve_via_toast),
                textContent(R.string.resolve_via_toast_explainer),
            ),
            SwitchPreferenceItemNew(
                vm.resolveViaFailedToast,
                textContent(R.string.resolve_via_failed_toast),
                textContent(R.string.resolve_via_failed_toast_explainer),
            )
        )
    }
}

@Composable
fun NotificationSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: NotificationSettingsViewModel = koinViewModel(),
) {
    val notificationSettings = rememberTwoLinePreferenceGroup(viewModel) { vm ->
        NewNotificationSettingsRouteData.init(vm)
    }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.notifications), onBackPressed = onBackPressed) {
        group(list = notificationSettings) { data, padding, shape ->
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

