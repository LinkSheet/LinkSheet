package fe.linksheet.composable.page.settings.privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.column.shape.ClickableShapeListItem
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.composable.page.settings.privacy.analytics.rememberAnalyticDialog
import fe.linksheet.module.viewmodel.PrivacySettingsViewModel
import fe.linksheet.util.buildconfig.Build
import org.koin.androidx.compose.koinViewModel


@Composable
fun PrivacySettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: PrivacySettingsViewModel = koinViewModel(),
) {
    val telemetryLevel by viewModel.telemetryLevel.collectAsStateWithLifecycle()
    val analyticsDialog = rememberAnalyticDialog(
        telemetryLevel = telemetryLevel,
        onChanged = { viewModel.updateTelemetryLevel(it) }
    )

    val enableAnalytics by viewModel.enableAnalytics.collectAsStateWithLifecycle()

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.privacy), onBackPressed = onBackPressed) {
        group(1) {
            item(key = R.string.show_linksheet_referrer) { padding, shape ->
                PreferenceSwitchListItem(
                    statePreference = viewModel.showAsReferrer,
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.show_linksheet_referrer),
                    supportingContent = textContent(R.string.show_linksheet_referrer_explainer),
                )
            }
        }

        if (Build.IsDebug || enableAnalytics) {
            divider(key = R.string.telemetry_configure_title, id = R.string.telemetry_configure_title)

            group(2) {
                item(key = R.string.telemetry_configure_type) { padding, shape ->
                    ClickableShapeListItem(
                        shape = shape,
                        padding = padding,
                        onClick = analyticsDialog::open,
                        role = Role.Button,
                        headlineContent = textContent(R.string.telemetry_configure_type),
                        supportingContent = textContent(telemetryLevel.titleId)
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
