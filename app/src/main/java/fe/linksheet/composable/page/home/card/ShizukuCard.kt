package fe.linksheet.composable.page.home.card

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import dev.zwander.shared.ShizukuUtil
import fe.android.compose.icon.DrawableIconPainter
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.card.AlertCard
import fe.linksheet.R
import fe.linksheet.module.shizuku.ShizukuStatus
import fe.linksheet.util.ShizukuDownload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
//    val icon = DrawableIconPainter.drawable(R.drawable.app_shizuku)

    AlertCard(
        onClick = {
            when (status) {
                ShizukuStatus.NoPermission -> scope.launch(Dispatchers.IO) {
                    if (ShizukuUtil.requestPermission()) {
                        status = ShizukuStatus.findStatus(shizukuInstalled, shizukuRunning, shizukuPermission)
                    }
                }

                ShizukuStatus.NotInstalled -> uriHandler.openUri(ShizukuDownload)
                else -> ShizukuUtil.startManager(activity)
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = if (status == ShizukuStatus.Enabled) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.tertiaryContainer
        ),
        icon = if (status == ShizukuStatus.Enabled) Icons.Rounded.Check.iconPainter else Icons.Outlined.Warning.iconPainter,
        iconContentDescription = stringResource(if (status == ShizukuStatus.Enabled) R.string.checkmark else R.string.error),
        headline = textContent(R.string.shizuku_integration),
        subtitle = textContent(statusStringId)
    )
}
