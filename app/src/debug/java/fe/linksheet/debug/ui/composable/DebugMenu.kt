package fe.linksheet.debug.ui.composable

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fe.linksheet.activity.onboarding.OnboardingActivity
import fe.linksheet.debug.activity.*
import fe.linksheet.experiment.improved.resolver.activity.bottomsheet.ImprovedBottomSheet
import fe.linksheet.ui.LocalActivity
import kotlin.reflect.KClass

@Composable
fun DebugMenu() {
    val activity = LocalActivity.current

    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        item(key = "onboarding") {
            FilledTonalActivityLauncher(
                activity = activity,
                text = "Launch onboarding",
                intent = createIntent(activity, OnboardingActivity::class)
            )
        }

        item(key = "export_log_dialog") {
            FilledTonalActivityLauncher(
                activity = activity,
                text = "Export log dialog testing",
                intent = createIntent(activity, ExportLogDialogTestActivity::class)
            )
        }

        item(key = "link_menu") {
            FilledTonalActivityLauncher(
                activity = activity,
                text = "Link testing",
                intent = createIntent(activity, LinkTestingActivity::class)
            )
        }

        item(key = "snap_tester") {
            FilledTonalActivityLauncher(
                activity = activity,
                text = "Snap tester",
                intent = createIntent(activity, DebugActivity::class)
            )
        }

        item(key = "url_preview") {
            FilledTonalActivityLauncher(
                activity = activity,
                text = "Url preview",
                intent = createIntent(activity, ComposableRendererActivity::class)
            )
        }

        item(key = "improved_bottomsheet") {
            FilledTonalActivityLauncher(
                activity = activity,
                text = "Improved bottom sheet",
                intent = createIntent(activity, ImprovedBottomSheet::class).setAction(Intent.ACTION_VIEW).setData(
                    Uri.parse("https://www.youtube.com/watch?v=XaqdBRHG9cI")
                )
            )
        }
    }
}

private fun createIntent(activity: Activity, activityClass: KClass<*>): Intent {
    return Intent(activity, activityClass.java)
}


@Composable
private fun FilledTonalActivityLauncher(activity: Activity, text: String, intent: Intent) {

    FilledTonalButton(
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        onClick = { activity.startActivity(intent) }
    ) {
        Text(text = text)
    }
}
