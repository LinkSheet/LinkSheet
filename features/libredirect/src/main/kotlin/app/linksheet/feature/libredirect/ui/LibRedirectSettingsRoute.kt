package app.linksheet.feature.libredirect.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import app.linksheet.compose.extension.listHelper
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.compose.util.listState
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import app.linksheet.feature.libredirect.viewmodel.LibRedirectSettingsViewModel
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.route.Route
import app.linksheet.feature.libredirect.R
import app.linksheet.feature.libredirect.navigation.LibRedirectServiceRoute
import app.linksheet.compose.extension.collectOnIO
import fe.linksheet.web.HostUtil
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalResources

@Composable
fun LibRedirectSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (Route) -> Unit,
    viewModel: LibRedirectSettingsViewModel = koinViewModel(),
) {
    val services by viewModel.services.collectOnIO(null)
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
    val resources = LocalResources.current
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
