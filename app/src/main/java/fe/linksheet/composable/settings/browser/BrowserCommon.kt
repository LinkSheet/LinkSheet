package fe.linksheet.composable.settings.browser

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.junkfood.seal.ui.component.PreferenceSubtitle
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.DialogColumn
import fe.linksheet.composable.util.DialogContent
import fe.linksheet.composable.util.DialogSpacer
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.extension.updateState
import fe.linksheet.extension.updateStateFromResult
import fe.linksheet.module.preference.BasePreference
import fe.linksheet.module.preference.RepositoryState
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.InAppBrowserDisableInSelected

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T, M> BrowserCommonScaffold(
    @StringRes headline: Int,
    @StringRes explainer: Int,
    onBackPressed: () -> Unit,
    viewModel: BaseViewModel,
    rowKey: (BrowserCommonRadioButtonRowData<T, M>) -> String,
    rows: List<BrowserCommonRadioButtonRowData<T, M>>,
    content: (LazyListScope.() -> Unit)? = null,
) {
    SettingsScaffold(headline = headline, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            stickyHeader(key = "header") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(
                        text = stringResource(explainer),
                        paddingStart = 0.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            items(items = rows, key = { rowKey(it) }) { row ->
                BrowserCommonRadioButtonRow(
                    value = row.value,
                    state = row.state,
                    viewModel = viewModel,
                    headline = row.headline,
                    subtitle = row.subtitle,
                    clickHook = row.clickHook
                )
            }

            content?.invoke(this)
        }
    }
}

data class BrowserCommonRadioButtonRowData<T, M>(
    val value: T,
    val state: RepositoryState<T, T, BasePreference.MappedPreference<T, M>>,
    @StringRes val headline: Int,
    @StringRes val subtitle: Int? = null,
    val clickHook: (() -> Unit)? = null
)

@Composable
fun <T, M> BrowserCommonRadioButtonRow(
    value: T,
    state: RepositoryState<T, T, BasePreference.MappedPreference<T, M>>,
    viewModel: BaseViewModel,
    @StringRes headline: Int,
    @StringRes subtitle: Int? = null,
    clickHook: (() -> Unit)? = null
) {
    RadioButtonRow(
        value = value,
        state = state,
        viewModel = viewModel,
        clickHook = clickHook,
    ) {
        Texts(
            headline = headline,
            subtitle = subtitle
        )
    }
}

@Composable
fun BrowserCommonDialog(
    @StringRes title: Int,
    state: InAppBrowserDisableInSelected?,
    alwaysShowPackageName: Boolean,
    close: (InAppBrowserDisableInSelected?) -> Unit,
) {
    DialogColumn {
        HeadlineText(headline = title)
        DialogSpacer()
        DialogContent(
            items = state,
            key = { it.flatComponentName },
            bottomRow = {
                TextButton(onClick = { close(state) }) {
                    Text(text = stringResource(id = R.string.save))
                }
            },
            content = { info, enabled ->
                val enabledState = remember { mutableStateOf(enabled) }
                val update: (Boolean) -> Unit = remember { { state?.set(info, it) } }

                ClickableRow(
                    verticalAlignment = Alignment.CenterVertically,
                    padding = 2.dp,
                    onClick = enabledState.updateState(update)
                ) {
                    Checkbox(
                        checked = enabledState.value,
                        onCheckedChange = enabledState.updateStateFromResult(update)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    BrowserIconTextRow(
                        app = info,
                        selected = enabled,
                        showSelectedText = false,
                        alwaysShowPackageName = alwaysShowPackageName
                    )
                }
            }
        )
    }
}