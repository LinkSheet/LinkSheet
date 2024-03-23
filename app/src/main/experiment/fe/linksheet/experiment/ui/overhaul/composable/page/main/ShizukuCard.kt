package fe.linksheet.experiment.ui.overhaul.composable.page.main

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
import androidx.compose.material.icons.filled.CrueltyFree
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
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.ClickableAlertListItem
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.module.shizuku.ShizukuStatus
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

    ClickableAlertListItem(
        onClick = {
            when (status) {
                ShizukuStatus.NoPermission -> scope.launch(Dispatchers.IO) {
                    if (ShizukuUtil.requestPermission()) {
                        status = ShizukuStatus.findStatus(shizukuInstalled, shizukuRunning, shizukuPermission)
                    }
                }

                ShizukuStatus.NotInstalled -> uriHandler.openUri(shizukuDownload)
                else -> ShizukuUtil.startManager(activity)
            }
        },
        colors = ShapeListItemDefaults.colors(
            containerColor = if (status == ShizukuStatus.Enabled) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.tertiaryContainer
        ),
        imageVector = if (status == ShizukuStatus.Enabled) Icons.Default.CrueltyFree else Icons.Default.Warning,
        contentDescriptionTextId = if (status == ShizukuStatus.Enabled) R.string.checkmark else R.string.error,
        headlineContentTextId = R.string.shizuku_integration,
        supportingContentTextId = statusStringId
    )
}
