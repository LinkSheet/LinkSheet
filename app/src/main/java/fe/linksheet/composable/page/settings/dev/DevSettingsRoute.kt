package fe.linksheet.composable.page.settings.dev

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.R
import fe.linksheet.activity.onboarding.OnboardingActivity
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun DevSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: DevSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.dev), onBackPressed = onBackPressed) {
        group(size = 1) {
            item(key = R.string.launch_onboarding_new) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    headlineContent = textContent(R.string.launch_onboarding_new),
                    supportingContent = textContent(R.string.launch_onboarding__now_explainer),
                    icon = Icons.AutoMirrored.Outlined.OpenInNew.iconPainter,
                    shape = shape,
                    padding = padding,
                    onClick = {
                        context.startActivity(Intent(context, OnboardingActivity::class.java))
                    }
                )
            }
        }
    }
}
