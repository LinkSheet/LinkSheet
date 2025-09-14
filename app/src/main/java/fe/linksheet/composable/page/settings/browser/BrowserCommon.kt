package fe.linksheet.composable.page.settings.browser

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.android.preference.helper.Preference
import fe.composekit.preference.ViewModelStatePreference
import fe.linksheet.R
import fe.linksheet.composable.page.settings.SettingsScaffold
import fe.linksheet.composable.util.*
import app.linksheet.compose.extension.items
import fe.linksheet.feature.app.ActivityAppInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Any, M : Any> BrowserCommonScaffold(
    navController: NavHostController,
    @StringRes headline: Int,
    @StringRes explainer: Int,
    onBackPressed: () -> Unit,
    values: List<T>,
    state:  ViewModelStatePreference<T, T, Preference.Mapped<T, M>>?,
    rowKey: (T) -> String,
    rows: List<BrowserCommonRadioButtonRowData>,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    browsers: List<ActivityAppInfo>? = null,
    selectorData: BrowserCommonPackageSelectorData<T>,
    content: (LazyListScope.(List<ActivityAppInfo>?) -> Unit)? = null,
) {
    SettingsScaffold(headlineId = headline, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "header") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(
                        text = stringResource(explainer),
                        paddingHorizontal = 10.dp
                    )

                    header?.invoke(this)

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }


            if (state != null) {
                val map = values.zip(rows).toMap()
                items(items = map, key = { rowKey(it) }) { value, row ->
                    BrowserCommonRadioButtonRow(
                        value = value,
                        state = state,
                        headlineId = row.headline,
                        subtitleId = row.subtitle,
                        clickHook = row.clickHook
                    )
                }


                item(key = "selector") {
                    DividedRow(
                        paddingHorizontal = 0.dp,
                        paddingVertical = 0.dp,
                        onLeftClick = { state(selectorData.value) },
                        leftContent = {
                            BrowserCommonRadioButtonRow(
                                value = selectorData.value,
                                state = state,
                                headlineId = selectorData.headline,
                                subtitleId = selectorData.subtitle
                            )
                        },
                        rightContent = {
                            IconButton(onClick = {
                                navController.navigate(selectorData.route)
                            }) {
                                ColoredIcon(
                                    icon = Icons.Default.Settings,
                                    descriptionId = R.string.settings,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }

            content?.invoke(this, browsers)
        }
    }
}

data class BrowserCommonPackageSelectorData<T>(
    @StringRes val headline: Int,
    @StringRes val subtitle: Int? = null,
    val value: T,
    val route: String,
)

data class BrowserCommonRadioButtonRowData(
    @StringRes val headline: Int,
    @StringRes val subtitle: Int? = null,
    val clickHook: (() -> Unit)? = null
)

@Composable
fun <T : Any, M : Any> BrowserCommonRadioButtonRow(
    modifier: Modifier = Modifier,
    value: T,
    state: ViewModelStatePreference<T, T, Preference.Mapped<T, M>>,
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int? = null,
    clickHook: (() -> Unit)? = null
) {
    RadioButtonRow(
        modifier = modifier,
        value = value,
        statePreference = state,
        clickHook = clickHook,
    ) {
        Texts(
            headlineId = headlineId,
            subtitleId = subtitleId
        )
    }
}

//@Composable
//fun BrowserCommonDialog(
//    @StringRes title: Int,
//    state: InAppBrowserDisableInSelected?,
//    alwaysShowPackageName: Boolean,
//    close: (InAppBrowserDisableInSelected?) -> Unit,
//) {
//    DialogColumn {
//        HeadlineText(headlineId = title)
//        DialogSpacer()
//        DialogContent(
//            items = state,
//            key = { it.flatComponentName },
//            bottomRow = {
//                TextButton(onClick = { close(state) }) {
//                    Text(text = stringResource(id = R.string.save))
//                }
//            },
//            content = { info, enabled ->
//                val enabledState = remember { mutableStateOf(enabled) }
//                val update: (Boolean) -> Unit = remember { { state?.set(info, it) } }
//
//                ClickableRow(
//                    verticalAlignment = Alignment.CenterVertically,
//                    onClick = enabledState.updateState(update)
//                ) {
//                    Checkbox(
//                        checked = enabledState.value,
//                        onCheckedChange = enabledState.updateStateFromResult(update)
//                    )
//
//                    Spacer(modifier = Modifier.width(5.dp))
//
//                    BrowserIconTextRow(
//                        app = info,
//                        selected = enabled,
//                        showSelectedText = false,
//                        alwaysShowPackageName = alwaysShowPackageName
//                    )
//                }
//            }
//        )
//    }
//}
