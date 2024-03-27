package fe.linksheet.debug.composeable

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
import fe.linksheet.debug.activity.DebugActivity
import fe.linksheet.ui.LocalActivity

@Composable
fun DebugMainRoute() {
    val activity = LocalActivity.current

    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        item(key = "snap_tester") {
            FilledTonalButton(
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                onClick = { activity.startActivity(Intent(activity, DebugActivity::class.java)) }
            ) {
                Text(text = "Snap tester")
            }
        }
    }
}
