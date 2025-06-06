package fe.linksheet.activity.bottomsheet.content.pending

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.preview.PreviewContainer
import app.linksheet.preview.rememberInfiniteEnumTransition
import fe.linksheet.R
import fe.linksheet.module.resolver.ResolveEvent
import fe.linksheet.module.resolver.ResolverInteraction

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun M3ELoadingIndicatorSheetContent(
    modifier: Modifier = Modifier,
    event: ResolveEvent,
    interaction: ResolverInteraction,
    requestExpand: () -> Unit,
) {
    LaunchedEffect(key1 = interaction) {
        Log.d(
            "LoadingIndicatorSheetContent",
            "Interaction=$interaction, isClear=${interaction == ResolverInteraction.Clear}, " +
                    "isInitialized=${interaction == ResolverInteraction.Initialized}"
        )
        if (interaction != ResolverInteraction.Initialized) {
            // Request resize on interaction change to accommodate interaction UI
            requestExpand()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.loading_link),
                style = MaterialTheme.typography.titleMedium
            )

            Text(text = stringResource(id = event.id, *event.args), style = MaterialTheme.typography.bodyMedium)
        }

        if (LocalInspectionMode.current) {
            ContainedLoadingIndicator(
                progress = { 0.7f }
            )
//            CircularWavyProgressIndicator()
//            LoadingIndicator(
//                progress = { 0.7f },
//            )
        } else {
            ContainedLoadingIndicator()
//            LoadingIndicator()
        }

        if (interaction is ResolverInteraction.Cancelable) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                val size = ButtonDefaults.MediumContainerHeight
                Button(
//                    modifier = Modifier.heightIn(size),
                    contentPadding =
                        PaddingValues(
                            start = 62.dp,
                            top = 12.dp,
                            end = 62.dp,
                            bottom = 12.dp
                        ),
                    onClick = {
                        Log.d("Interact", "Cancel")
                        interaction.cancel()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.bottom_sheet_loading_indicator__button_skip_job),
//                        style = ButtonDefaults.textStyleFor(size)
                    )
                }

            }
        }
//        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp))
    }
}


@Preview(showBackground = true)
@Composable
private fun M3ELoadingIndicatorPreview() {
    val event = rememberInfiniteEnumTransition<ResolveEvent>()
    val interaction = remember(event) {
        if (event == ResolveEvent.GeneratingPreview) ResolverInteraction.Cancelable(event) { }
        else ResolverInteraction.Clear
    }

    M3ELoadingIndicatorPreviewBase(event = event, interaction = interaction)
}

@Preview(showBackground = true)
@Composable
private fun M3ELoadingIndicatorPreviewCancelable() {
    val event = ResolveEvent.GeneratingPreview
    val interaction = remember(event) {
        ResolverInteraction.Cancelable(event) { }
    }

    M3ELoadingIndicatorPreviewBase(event = event, interaction = interaction)
}

@Composable
private fun M3ELoadingIndicatorPreviewBase(event: ResolveEvent, interaction: ResolverInteraction) {
    PreviewContainer {
        M3ELoadingIndicatorSheetContent(
            event = event,
            interaction = interaction,
            requestExpand = {}
        )
    }
}
