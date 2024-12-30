package fe.linksheet.composable.page.settings.link.libredirect

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.navigation.NavController
import fe.android.compose.route.util.navigate
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.component.list.item.type.SwitchListItem
import fe.linksheet.navigation.LibRedirectServiceRoute
import fe.linksheet.R
import fe.linksheet.composable.util.listState
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.navigation.libRedirectServiceSettingsRoute
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.LibRedirectSettingsViewModel
import fe.linksheet.util.net.HostUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewLibRedirectSettingsRoute(
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
        item(key = R.string.enable_libredirect, contentType = ContentType.SingleGroupItem) {
            SwitchListItem(
                checked = viewModel.enableLibRedirect(),
                onCheckedChange = { viewModel.enableLibRedirect(it) },
                position = ContentPosition.Trailing,
                headlineContent = textContent(R.string.enable_libredirect),
                supportingContent = annotatedStringResource(R.string.enable_libredirect_explainer),
            )
        }

        divider(id =  R.string.services)

        listHelper(
            noItems = R.string.no_libredirect_services,
            listState = listState,
            list = services,
            listKey = { it.service.key },
        ) { service, padding, shape ->
            DefaultTwoLineIconClickableShapeListItem(
                padding = padding,
                shape = shape,
                headlineContent = text(service.service.name),
                supportingContent = content {
                    Column {
                        Text(text = HostUtil.cleanHttpsScheme(service.service.url))
                        if (service.enabled) {
                            Text(
                                fontStyle = FontStyle.Italic,
                                text = stringResource(
                                    id = R.string.libredirect_via,
                                    instanceUrl(
                                        instance = service.instance
                                            ?: stringResource(id = R.string.instance_not_available_anymore)
                                    )
                                )
                            )
                        }
                    }
                },
                onClick = {
                    navController.navigate(
                        libRedirectServiceSettingsRoute,
                        LibRedirectServiceRoute(service.service.key)
                    )
                }
            )
        }
    }
}

@Composable
private fun instanceUrl(
    instance: String,
): String {
    return if (instance == LibRedirectDefault.randomInstance) stringResource(
        id = R.string.random_instance
    ) else HostUtil.cleanHttpsScheme(instance)
}
