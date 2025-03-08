package fe.linksheet.composable.page.settings.link.libredirect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.route.Route
import fe.linksheet.navigation.LibRedirectServiceRoute
import fe.linksheet.R
import fe.linksheet.composable.component.list.item.type.PreferenceSwitchListItem
import fe.linksheet.composable.util.listState
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.extension.compose.listHelper
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.LibRedirectSettingsViewModel
import fe.linksheet.util.web.HostUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewLibRedirectSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (Route) -> Unit,
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
            PreferenceSwitchListItem(
                statePreference = viewModel.enableLibRedirect,
                headlineContent = textContent(R.string.enable_libredirect),
                supportingContent = annotatedStringResource(R.string.enable_libredirect_explainer),
            )
        }

        divider(id = R.string.services)

        listHelper(
            noItems = R.string.no_libredirect_services,
            listState = listState,
            list = services,
            listKey = { it.service.key },
        ) { service, padding, shape ->
            LibRedirectServiceRow(service = service, padding = padding, shape = shape, navigate = navigate)
        }
    }
}

@Composable
private fun LibRedirectServiceRow(
    service: LibRedirectSettingsViewModel.LibRedirectServiceWithInstance,
    padding: PaddingValues,
    shape: Shape,
    navigate: (Route) -> Unit,
) {
    DefaultTwoLineIconClickableShapeListItem(
        padding = padding,
        shape = shape,
        onClick = { navigate(LibRedirectServiceRoute(service.service.key)) },
        headlineContent = text(service.service.name),
        supportingContent = content {
            Column {
                Text(text = HostUtil.cleanHttpsScheme(service.service.url))
                if (service.enabled) {
                    InstanceUrlText(instance = service.instance)
                }
            }
        }
    )
}

@Composable
private fun InstanceUrlText(instance: String?) {
    val resources = LocalContext.current.resources
    val args = remember(instance) {
        when (instance) {
            null -> resources.getString(R.string.instance_not_available_anymore)
            LibRedirectDefault.randomInstance -> resources.getString(R.string.random_instance)
            else -> HostUtil.cleanHttpsScheme(instance)
        }
    }

    Text(
        fontStyle = FontStyle.Italic,
        text = stringResource(
            id = R.string.libredirect_via,
            args
        )
    )
}
