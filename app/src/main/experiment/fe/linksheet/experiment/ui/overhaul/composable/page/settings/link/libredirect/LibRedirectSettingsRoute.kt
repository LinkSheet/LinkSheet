package fe.linksheet.experiment.ui.overhaul.composable.page.settings.link.libredirect

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import fe.linksheet.R
import fe.linksheet.composable.util.*
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.SwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneLazyListScope
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.compose.loader
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.LibRedirectSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibRedirectSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavController,
    viewModel: LibRedirectSettingsViewModel = koinViewModel(),
) {
    val services by viewModel.services.collectOnIO()

    val listState = remember(services?.size) {
        listState(services)
    }

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.lib_redirect),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.enable_libredirect, contentType = ContentTypeDefaults.SingleGroupItem) {
            SwitchListItem(
                checked = viewModel.enableLibRedirect(),
                onCheckedChange = { viewModel.enableLibRedirect(it) },
                headlineContent = {
                    Text(text = stringResource(id = R.string.enable_libredirect))
                },
                supportingContent = {
                    LinkableTextView(
                        id = R.string.enable_libredirect_explainer,
                        style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                }
            )
        }

        divider(stringRes = R.string.services)

//        group() {
//
//        }

        listHelper(
            noItems = R.string.no_libredirect_services,
            listState = listState,
            list = services,
            listKey = { it.service.key },
        ) { (service, enabled, instance) ->
//            ClickableRow(
//                onClick = {
//                    navController.navigate(
//                        libRedirectServiceSettingsRoute,
//                        LibRedirectServiceRoute(service.key)
//                    )
//                }
//            ) {
//                Texts(
//                    headline = service.name,
//                    subtitle = HostUtil. cleanHttpsScheme(service.url),
//                ) {
//                    if (enabled) {
//                        SubtitleText(
//                            fontStyle = FontStyle.Italic,
//                            subtitle = stringResource(
//                                id = R.string.libredirect_via,
//                                instanceUrl(instance = instance ?: stringResource(id = R.string.instance_not_available_anymore) )
//                            )
//                        )
//                    }
//                }
//            }
        }
    }

//    SettingsScaffold(R.string.lib_redirect, onBackPressed = onBackPressed) { padding ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxHeight(),
//            contentPadding = PaddingValues(horizontal = 5.dp)
//        ) {
//            stickyHeader(key = "enabled") {
//                SettingEnabledCardColumn(
//                    state = viewModel.enableLibRedirect,
//                    viewModel = viewModel,
//                    headline = stringResource(id = R.string.enable_libredirect),
//                    subtitle = null,
//                    subtitleBuilder = {
//                        LinkableTextView(
//                            id = R.string.enable_libredirect_explainer,
//                            style = LocalTextStyle.current.copy(
//                                color = MaterialTheme.colorScheme.onSurface,
//                                fontSize = 16.sp,
//                            )
//                        )
//                    },
//                    contentTitle = stringResource(id = R.string.services)
//                )
//            }
//

//        }
//    }
}

inline fun <T> SaneLazyListScope.listHelper(
    @StringRes noItems: Int,
    @StringRes notFound: Int? = null,
    listState: ListState,
    list: List<T>?,
    noinline listKey: (T) -> Any,
    crossinline listItem: @Composable LazyItemScope.(T) -> Unit,
) {
    if (listState == ListState.Items && list != null) {
        group(list.size) {
            items(items = list, key = listKey, itemContent = listItem)
        }
    } else {
        loader(noItems, notFound, listState)
    }
}


@Composable
private fun instanceUrl(
    instance: String,
) = if (instance == LibRedirectDefault.randomInstance) stringResource(
    id = R.string.random_instance
) else instance
