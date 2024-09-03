package fe.linksheet.experiment.improved.resolver.activity.bottomsheet

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.experiment.improved.resolver.ResolveEvent
import fe.linksheet.experiment.improved.resolver.ResolverInteraction
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.composable.ui.PreviewTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LoadingIndicator(
    events: StateFlow<ResolveEvent>,
    interactions: StateFlow<ResolverInteraction>,
    requestExpand: () -> Unit
) {
    val event by events.collectOnIO(initialState = ResolveEvent.Initialized)
    val interaction by interactions.collectOnIO(initialState = ResolverInteraction.Clear)

    LaunchedEffect(key1 = interaction) {
        requestExpand()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.loading_link),
            style = MaterialTheme.typography.titleMedium
        )

        Text(text = stringResource(id = event.id, *event.args), style = MaterialTheme.typography.bodyMedium)

        if (interaction is ResolverInteraction.Cancelable) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {

                Button(onClick = {
                    Log.d("Interact", "Cancel")
                    (interaction as? ResolverInteraction.Cancelable)?.cancel?.invoke()
                }) {
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
    val events = MutableStateFlow(ResolveEvent.Initialized)
    val interactions = MutableStateFlow<ResolverInteraction>(ResolverInteraction.Clear)

    LaunchedEffect(key1 = Unit) {
        var i = 0
        while (true) {
            val event = ResolveEvent.entries.random()
            events.emit(event)
            if (i % 5 == 0) {
                interactions.emit(ResolverInteraction.Cancelable(event) { Log.d("Preview", "Cancel clicked") })
            }

            delay(2000)


            i++
        }
    }

    PreviewTheme {
        LoadingIndicator(events = events, interactions = interactions, requestExpand = {

        })
    }
}
