package fe.linksheet.debug.ui.composable

import android.content.Intent
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
import fe.linksheet.debug.activity.ComposableRendererActivity
import fe.linksheet.debug.activity.DebugActivity
import fe.linksheet.ui.LocalActivity
import kotlin.reflect.KClass

@Composable
fun DebugMenu() {
    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        item(key = "snap_tester") {
            FilledTonalActivityLauncher(text = "Snap tester", activityClass = DebugActivity::class)
        }

        item(key = "url_preview") {
            FilledTonalActivityLauncher(text = "Url preview", activityClass = ComposableRendererActivity::class)
        }
    }
}

@Composable
private fun FilledTonalActivityLauncher(text: String, activityClass: KClass<*>) {
    val activity = LocalActivity.current

    FilledTonalButton(
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        onClick = { activity.startActivity(Intent(activity, activityClass.java)) }
    ) {
        Text(text = text)
    }
}
