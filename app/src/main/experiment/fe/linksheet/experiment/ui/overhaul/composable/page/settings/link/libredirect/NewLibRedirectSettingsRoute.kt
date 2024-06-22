package fe.linksheet.experiment.ui.overhaul.composable.page.settings.link.libredirect

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.navigation.NavController
import fe.android.compose.route.util.navigate
import fe.linksheet.LibRedirectServiceRoute
import fe.linksheet.R
import fe.linksheet.composable.util.listState
import fe.linksheet.component.ContentTypeDefaults
import fe.linksheet.component.list.base.ContentPosition
import fe.linksheet.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.component.list.item.type.SwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.component.util.AnnotatedStringResource.Companion.annotated
import fe.linksheet.component.util.ComposableTextContent.Companion.content
import fe.linksheet.component.util.Default.Companion.text
import fe.linksheet.component.util.Resource.Companion.textContent
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.libRedirectServiceSettingsRoute
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.LibRedirectSettingsViewModel
import fe.linksheet.util.HostUtil
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
        item(key = R.string.enable_libredirect, contentType = ContentTypeDefaults.SingleGroupItem) {
            SwitchListItem(
                checked = viewModel.enableLibRedirect(),
                onCheckedChange = { viewModel.enableLibRedirect(it) },
                position = ContentPosition.Trailing,
                headlineContent = textContent(R.string.enable_libredirect),
                supportingContent = annotated(R.string.enable_libredirect_explainer),
            )
        }

        divider(stringRes = R.string.services)

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
