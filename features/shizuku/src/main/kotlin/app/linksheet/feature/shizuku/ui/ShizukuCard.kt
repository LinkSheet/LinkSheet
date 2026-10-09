package app.linksheet.feature.shizuku.ui

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import app.linksheet.feature.shizuku.R
import app.linksheet.feature.shizuku.service.ShizukuStatus
import app.linksheet.feature.shizuku.usecase.ShizukuStatusUseCase
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.card.AlertCard
import fe.composekit.lifecycle.collectRefreshableAsStateWithLifecycle
import app.linksheet.compose.R as CommonR


@Composable
fun ShizukuCard(
    activity: Activity,
    useCase: ShizukuStatusUseCase
) {
    val status by useCase.status.collectRefreshableAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED
    )

    ShizukuCardInternal(
        status = status,
        openWeb = { useCase.openShizukuWeb(activity) },
        requestPermission = useCase::requestPermission,
        openManager = { useCase.startManager(activity) }
    )
}

@Composable
private fun ShizukuCardInternal(
    status: ShizukuStatus,
    openWeb: () -> Unit,
    openManager: () -> Unit,
    requestPermission: () -> Unit,
) {
    AlertCard(
        onClick = {
            when {
                !status.installed -> openWeb()
                !status.running -> openManager()
                !status.permission -> requestPermission()
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = if (status.allOk) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.tertiaryContainer
        ),
        icon = if (status.allOk) Icons.Rounded.Check.iconPainter else Icons.Rounded.Warning.iconPainter,
        iconContentDescription = stringResource(if (status.allOk) CommonR.string.checkmark else CommonR.string.error),
        headline = textContent(R.string.shizuku_integration),
        subtitle = textContent(status.stringRes)
    )
}

internal val ShizukuStatus.stringRes: Int
    get() = when {
        !installed -> R.string.shizuku_integration_not_setup_explainer
        !running -> R.string.shizuku_not_running_explainer
        !permission -> R.string.shizuku_integration_no_permission_explainer
        else -> R.string.shizuku_integration_enabled_explainer
    }
