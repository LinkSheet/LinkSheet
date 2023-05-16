package fe.linksheet.composable.settings.browser

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.junkfood.seal.ui.component.PreferenceSubtitle
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.DynamicHeightDialog
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.extension.sortByValueAndName
import fe.linksheet.module.preference.BasePreference
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.RepositoryState
import fe.linksheet.ui.theme.HkGroteskFontFamily
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T, M> BrowserCommonScaffold(
    @StringRes headline: Int,
    @StringRes explainer: Int,
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel,
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
    viewModel: SettingsViewModel,
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
    alwaysShowPackageName: Boolean,
    items: SnapshotStateMap<DisplayActivityInfo, Boolean>,
    updateActivityState: (activity: DisplayActivityInfo, state: Boolean) -> Unit,
    closeAndSave: () -> Unit,
): Pair<MutableState<Boolean>, MutableState<Boolean>> {
    val open = remember { mutableStateOf(false) }
    val contentLoaded = remember { mutableStateOf(false) }

    BrowserCommonDialog(
        open = open.value,
        title = title,
        contentLoaded = contentLoaded.value,
        alwaysShowPackageName = alwaysShowPackageName,
        items = items,
        updateActivityState = updateActivityState,
        close = { save ->
            open.value = false
            if (save) closeAndSave()
        }
    )

    return open to contentLoaded
}

@Composable
private fun BrowserCommonDialog(
    open: Boolean,
    @StringRes title: Int,
    contentLoaded: Boolean,
    alwaysShowPackageName: Boolean,
    items: SnapshotStateMap<DisplayActivityInfo, Boolean>,
    updateActivityState: (activity: DisplayActivityInfo, state: Boolean) -> Unit,
    close: (save: Boolean) -> Unit,
) {
    val context = LocalContext.current
    if (open) {
        DynamicHeightDialog(onDismissRequest = { close(false) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = title),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!contentLoaded) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                } else {
                    val browserItems = items.sortByValueAndName()
                    Timber.tag("BrowserCommon").d("BrowserItems: ${browserItems.size}")
                    Box {
                        LazyColumn(modifier = Modifier.padding(bottom = 40.dp)) {
                            items(
                                items = browserItems,
                                key = { item -> item.first.flatComponentName }
                            ) { (browser, enabled) ->
                                Timber.tag("BrowserCommon").d("$browser $enabled")
                                var state by remember { mutableStateOf(enabled) }

                                ClickableRow(
                                    verticalAlignment = Alignment.CenterVertically,
                                    padding = 2.dp,
                                    onClick = {
                                        updateActivityState(browser, !state)
                                        state = !state
                                    }
                                ) {
                                    Checkbox(checked = state, onCheckedChange = {
                                        updateActivityState(browser, it)
                                        state = it
                                    })

                                    Spacer(modifier = Modifier.width(5.dp))

                                    BrowserIconTextRow(
                                        context = context,
                                        app = browser,
                                        selected = enabled,
                                        showSelectedText = false,
                                        alwaysShowPackageName = alwaysShowPackageName
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .height(40.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { close(true) }) {
                                Text(text = stringResource(id = R.string.save))
                            }
                        }
                    }
                }
            }
        }
    }
}