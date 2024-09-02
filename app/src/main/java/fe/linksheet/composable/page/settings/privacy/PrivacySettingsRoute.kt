package fe.linksheet.composable.page.settings.privacy

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.column.shape.ClickableShapeListItem
import fe.linksheet.R
import fe.linksheet.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.composable.page.settings.privacy.analytics.rememberAnalyticDialog
import fe.linksheet.module.viewmodel.PrivacySettingsViewModel
import fe.linksheet.util.BuildType
import org.koin.androidx.compose.koinViewModel


@Composable
fun PrivacySettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: PrivacySettingsViewModel = koinViewModel(),
) {
    val analyticsDialog = rememberAnalyticDialog(
        telemetryLevel = viewModel.telemetryLevel(),
        onChanged = { viewModel.updateTelemetryLevel(it) }
    )

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.privacy), onBackPressed = onBackPressed) {
        group(1) {
            item(key = R.string.show_linksheet_referrer) { padding, shape ->
                PreferenceSwitchListItem(
                    preference = viewModel.showAsReferrer,
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.show_linksheet_referrer),
                    supportingContent = textContent(R.string.show_linksheet_referrer_explainer),
                )
            }
        }

        if (BuildType.current.allowDebug || viewModel.enableAnalytics()) {
            divider(key = R.string.telemetry_configure_title, id = R.string.telemetry_configure_title)

            group(2) {
                item(key = R.string.telemetry_configure_type) { padding, shape ->
                    ClickableShapeListItem(
                        shape = shape,
                        padding = padding,
                        onClick = analyticsDialog::open,
                        role = Role.Button,
                        headlineContent = textContent(R.string.telemetry_configure_type),
                        supportingContent = textContent(viewModel.telemetryLevel().titleId)
                    )
                }

                item(key = R.string.telemetry_identifier_reset) { padding, shape ->
                    ClickableShapeListItem(
                        shape = shape,
                        padding = padding,
                        onClick = { viewModel.resetIdentifier() },
                        role = Role.Button,
                        headlineContent = textContent(R.string.telemetry_identifier_reset)
                    )
                }
            }
        }
    }
}