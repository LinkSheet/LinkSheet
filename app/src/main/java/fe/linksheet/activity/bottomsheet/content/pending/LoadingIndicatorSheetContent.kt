package fe.linksheet.activity.bottomsheet.content.pending

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewContainer
import app.linksheet.compose.preview.rememberInfiniteEnumTransition
import fe.linksheet.R
import fe.linksheet.module.resolver.ResolveEvent
import fe.linksheet.module.resolver.ResolverInteraction

@Composable
fun LoadingIndicatorSheetContent(
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
//        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        if (LocalInspectionMode.current) {
            CircularProgressIndicator(
                progress = { 0.7f },
            )
        } else {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.loading_link),
            style = MaterialTheme.typography.titleMedium
        )

        Text(text = stringResource(id = event.id, *event.args), style = MaterialTheme.typography.bodyMedium)

        if (interaction is ResolverInteraction.Cancelable) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = {
                        Log.d("Interact", "Cancel")
                        interaction.cancel()
                    }
                ) {
                    Text(text = stringResource(id = R.string.bottom_sheet_loading_indicator__button_skip_job))
                }
            }
        }
//        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp))
    }
}



@Preview(showBackground = true)
@Composable
private fun LoadingIndicatorPreview() {
    val event = rememberInfiniteEnumTransition<ResolveEvent>()
    val interaction = remember(event) {
        if(event == ResolveEvent.GeneratingPreview) ResolverInteraction.Cancelable(event) {  }
        else ResolverInteraction.Clear
    }

    LoadingIndicatorPreviewBase(event = event, interaction = interaction)
}

@Composable
private fun LoadingIndicatorPreviewBase(event: ResolveEvent, interaction: ResolverInteraction) {
    PreviewContainer {
        LoadingIndicatorSheetContent(
            event = event,
            interaction = interaction,
            requestExpand = {}
        )
    }
}
