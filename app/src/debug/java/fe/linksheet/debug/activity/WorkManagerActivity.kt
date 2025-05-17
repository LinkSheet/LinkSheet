package fe.linksheet.debug.activity

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkInfo
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.composekit.component.card.AlertCard
import fe.kotlin.extension.string.capitalize
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.module.remoteconfig.RemoteAssetFetcherWorker
import fe.linksheet.module.workmanager.WorkService
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class WorkManagerActivity : BaseComponentActivity(), KoinComponent {
    private val workService by inject<WorkService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            AppTheme {
                Box(modifier = Modifier.systemBarsPadding()) {
                    Column(
                        modifier = Modifier.padding(all = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WorkManagerScreen(this@WorkManagerActivity, workService)
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkManagerScreen(context: Context, workService: WorkService) {
    val infos by workService.workManager.getWorkInfosForUniqueWorkFlow(RemoteAssetFetcherWorker.tag)
        .collectAsStateWithLifecycle(
            initialValue = emptyList(),
        )

    LazyColumn {
        items(items = infos) {
            WorkInfoCard(
                id = it.id,
                state = it.state,
                nextScheduleTimeMillis = it.nextScheduleTimeMillis,
                tags = it.tags
            )
        }
    }
}

//    WorkInfo{id='$id', state=$state, " +
//        "outputData=$outputData, tags=$tags, progress=$progress, " +
//                "runAttemptCount=$runAttemptCount, generation=$generation, " +
//                "constraints=$constraints, initialDelayMillis=$initialDelayMillis, " +
//                "periodicityInfo=$periodicityInfo, " +
//                "nextScheduleTimeMillis=$nextScheduleTimeMillis}, " +
//                "stopReason=$stopReason

private fun WorkInfo.State.toIcon(): ImageVector {
    return when (this) {
        WorkInfo.State.ENQUEUED -> Icons.Outlined.Schedule
        WorkInfo.State.RUNNING -> Icons.Outlined.PlayArrow
        WorkInfo.State.SUCCEEDED -> Icons.Outlined.CheckCircle
        WorkInfo.State.FAILED -> Icons.Outlined.ErrorOutline
        WorkInfo.State.BLOCKED -> Icons.Outlined.Block
        WorkInfo.State.CANCELLED -> Icons.Outlined.Stop
    }
}

@Composable
private fun WorkInfoCard(id: UUID, state: WorkInfo.State, nextScheduleTimeMillis: Long, tags: Set<String>) {
    val next = remember(nextScheduleTimeMillis) {
        nextScheduleTimeMillis.unixMillisUtc.format(ISO8601DateTimeFormatter.FriendlyFormat)
    }

    AlertCard(
        colors = CardDefaults.cardColors(
//            containerColor = containerColor
        ),
        icon = state.toIcon().iconPainter,
        iconContentDescription = null,
        headline = text(state.name.lowercase().capitalize()),
//        subtitle = text("$id"),
        subtitle = content {
            Column {
                Text(text = id.toString())
                Text(text = "Next @ $next")
            }
        }
    ) {
        Column {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items = tags.toList()) {
                    SuggestionChip(
                        label = {
                            Text(text = it)
                        },
                        onClick = {}
                    )
                }
            }
        }
    }
}


private class WorkInfoStateProvider() : PreviewParameterProvider<WorkInfo.State> {
    override val values: Sequence<WorkInfo.State>
        get() = WorkInfo.State.entries.asSequence()

}

@Preview
@Composable
private fun WorkInfoCardPreview(@PreviewParameter(WorkInfoStateProvider::class) state: WorkInfo.State) {
    WorkInfoCard(
        id = UUID.randomUUID(),
        state = state,
        nextScheduleTimeMillis = System.currentTimeMillis(),
        tags = setOf("remote-assets", "fe.linksheet.module.remoteconfig.RemoteAssetFetcherWorker"),
    )
}
