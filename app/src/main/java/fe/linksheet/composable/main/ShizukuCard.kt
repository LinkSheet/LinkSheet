package fe.linksheet.composable.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zwander.shared.ShizukuUtil
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.shizuku.ShizukuStatus
import fe.linksheet.shizukuDownload
import fe.linksheet.ui.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


private val statusMap = mapOf(
    ShizukuStatus.Enabled to R.string.shizuku_integration_enabled_explainer,
    ShizukuStatus.NotRunning to R.string.shizuku_not_running_explainer,
    ShizukuStatus.NoPermission to R.string.shizuku_integration_no_permission_explainer,
    ShizukuStatus.NotInstalled to R.string.shizuku_integration_not_setup_explainer,
)

@Composable
fun ShizukuCard(
    activity: Activity,
    uriHandler: UriHandler,
    shizukuInstalled: Boolean,
    shizukuRunning: Boolean,
) {
    val shizukuPermission by ShizukuUtil.rememberHasShizukuPermissionAsState()

    var status = ShizukuStatus.findStatus(shizukuInstalled, shizukuRunning, shizukuPermission)
    val statusStringId = statusMap[status]!!

    val scope = rememberCoroutineScope()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (status == ShizukuStatus.Enabled) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.tertiaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .clickable {
                    when (status) {
                        ShizukuStatus.NoPermission -> scope.launch(Dispatchers.IO) {
                            if (ShizukuUtil.requestPermission()) {
                                status = ShizukuStatus.findStatus(shizukuInstalled, shizukuRunning, shizukuPermission)
                            }
                        }

                        ShizukuStatus.NotInstalled -> uriHandler.openUri(shizukuDownload)
                        else -> ShizukuUtil.startManager(activity)
                    }
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            val color = if (status == ShizukuStatus.Enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onTertiaryContainer

            Spacer(modifier = Modifier.width(10.dp))
            ColoredIcon(
                icon = if (status == ShizukuStatus.Enabled) Icons.Default.CheckCircle else Icons.Default.Warning,
                descriptionId = if (status == ShizukuStatus.Enabled) R.string.checkmark else R.string.error,
                color = color
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(id = R.string.shizuku_integration),
                    style = Typography.titleLarge,
                    color = color
                )
                Text(
                    text = stringResource(id = statusStringId),
                    color = color
                )
            }
        }
    }
}
