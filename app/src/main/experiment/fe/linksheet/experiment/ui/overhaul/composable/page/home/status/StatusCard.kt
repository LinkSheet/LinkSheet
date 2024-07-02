package fe.linksheet.experiment.ui.overhaul.composable.page.home.status

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.component.card.ClickableAlertCard2
import fe.linksheet.component.util.Resource.Companion.textContent
import fe.linksheet.experiment.ui.overhaul.ui.PreviewThemeNew
import fe.linksheet.module.viewmodel.MainViewModel

@Composable
private fun cardContainerColor(isDefaultBrowser: Boolean): Color {
    return if (isDefaultBrowser) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.errorContainer
}

@Composable
private fun buttonColor(isDefaultBrowser: Boolean): Color {
    return if (isDefaultBrowser) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.error
}

@Composable
internal fun StatusCard(
    isDefaultBrowser: Boolean,
    launchIntent: (MainViewModel.SettingsIntent) -> Unit,
    onSetAsDefault: () -> Unit
) {
    val containerColor = cardContainerColor(isDefaultBrowser)
    val buttonColor = buttonColor(isDefaultBrowser)

    val icon = if (isDefaultBrowser) Icons.Rounded.CheckCircleOutline
    else Icons.Rounded.ErrorOutline

    val title = if (isDefaultBrowser) R.string.settings_main_setup_success__title_linksheet_setup_success
    else R.string.settings_main_setup_success__title_linksheet_setup_failure

    val subtitle = if (isDefaultBrowser) R.string.settings_main_setup_success__text_linksheet_setup_success_info
    else R.string.settings_main_setup_success__text_linksheet_setup_failure_info

    ClickableAlertCard2(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        contentDescription = null,
        headline = textContent(title),
        subtitle = textContent(subtitle),
        imageVector = icon,
    ) {
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            if (isDefaultBrowser) {
                item(key = R.string.settings_main_setup_success__button_change_browser) {
                    StatusCardButton(
                        id = R.string.settings_main_setup_success__button_change_browser,
                        buttonColor = buttonColor,
                        onClick = { launchIntent(MainViewModel.SettingsIntent.DefaultApps) }
                    )
                }

                item(key = R.string.settings_main_setup_success__button_link_handlers) {
                    StatusCardButton(
                        id = R.string.settings_main_setup_success__button_link_handlers,
                        buttonColor = buttonColor,
                        onClick = { launchIntent(MainViewModel.SettingsIntent.DomainUrls) }
                    )
                }

                item(key = R.string.settings_main_setup_success__button_connected_apps) {
                    StatusCardButton(
                        id = R.string.settings_main_setup_success__button_connected_apps,
                        buttonColor = buttonColor,
                        onClick = { launchIntent(MainViewModel.SettingsIntent.CrossProfileAccess) }
                    )
                }
            } else {
                item(key = R.string.settings_main_setup_success__button_set_default_browser) {
                    StatusCardButton(
                        id = R.string.settings_main_setup_success__button_set_default_browser,
                        buttonColor = buttonColor,
                        onClick = onSetAsDefault
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusCardButton(
    @StringRes id: Int,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        onClick = onClick
    ) {
        Text(text = stringResource(id = id))
    }
}

@Preview
@Composable
fun StatusCardPreview() {
    PreviewThemeNew {
        StatusCard(
            isDefaultBrowser = true,
            launchIntent = {},
            onSetAsDefault = {}
        )
    }
}

@Preview
@Composable
fun StatusCardPreviewNonDefault() {
    PreviewThemeNew {
        StatusCard(
            isDefaultBrowser = false,
            launchIntent = {},
            onSetAsDefault = {}
        )
    }
}
