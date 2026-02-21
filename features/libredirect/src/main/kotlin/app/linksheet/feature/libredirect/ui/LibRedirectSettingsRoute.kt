package app.linksheet.feature.libredirect.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import app.linksheet.compose.extension.collectOnIO
import app.linksheet.compose.extension.listHelper
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.compose.preview.PreviewContainer
import app.linksheet.compose.util.listState
import app.linksheet.feature.libredirect.LibRedirectData
import app.linksheet.feature.libredirect.R
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import app.linksheet.feature.libredirect.navigation.LibRedirectServiceRoute
import app.linksheet.feature.libredirect.viewmodel.LibRedirectSettingsViewModel
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.CommonDefaults
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.preference.BooleanVmPref
import fe.composekit.preference.fakeBooleanVM
import fe.composekit.route.Route
import fe.linksheet.web.HostUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibRedirectSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (Route) -> Unit,
    viewModel: LibRedirectSettingsViewModel = koinViewModel(),
) {
    val services by viewModel.services.collectOnIO(null)

    LibRedirectSettingsRouteInternal(
        onBackPressed = onBackPressed,
        navigate = navigate,
        enableLibRedirect = viewModel.enableLibRedirect,
        services = services
    )
}

const val LIBREDIRECT_SERVICES_LIST_TEST_TAG = "libredirect_services_list_test_tag"
val LIBREDIRECT_SERVICE_ROW_TEST_TAG: (String) -> String = { "libredirect_service_row__test_tag_$it" }

@Composable
internal fun LibRedirectSettingsRouteInternal(
    onBackPressed: () -> Unit,
    navigate: (Route) -> Unit,
    enableLibRedirect: BooleanVmPref,
    services: List<LibRedirectSettingsViewModel.LibRedirectServiceWithInstance>?,
) {
    val listState = remember(services?.size) {
        listState(services)
    }

    SaneScaffoldSettingsPage(
        modifier = Modifier.testTag(LIBREDIRECT_SERVICES_LIST_TEST_TAG),
        headline = stringResource(id = R.string.lib_redirect),
        onBackPressed = onBackPressed
    ) {
        item(key = R.string.enable_libredirect, contentType = ContentType.SingleGroupItem) {
            PreferenceSwitchListItem(
                statePreference = enableLibRedirect,
                headlineContent = textContent(R.string.enable_libredirect),
                supportingContent = annotatedStringResource(R.string.enable_libredirect_explainer),
            )
        }

        divider(id = R.string.services)

        listHelper(
            noItems = R.string.no_libredirect_services,
            notFound = null,
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
        modifier = CommonDefaults.BaseModifier.testTag(LIBREDIRECT_SERVICE_ROW_TEST_TAG(service.service.key)),
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

@Preview
@Composable
private fun LibRedirectSettingsRouteInternalPreview() {
    val services = remember {
        (1..100).map {
            LibRedirectSettingsViewModel.LibRedirectServiceWithInstance(
                service = LibRedirectData.RedditService.run { copy(key = key + it, name = name + it) },
                enabled = true,
                instance = null
            )
        }
    }
    PreviewContainer {
        LibRedirectSettingsRouteInternal(
            onBackPressed = {},
            navigate = {},
            enableLibRedirect = fakeBooleanVM(true),
            services = services
        )
    }
}
