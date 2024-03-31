package fe.linksheet.experiment.improved.resolver.activity.bottomsheet

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.experiment.improved.resolver.ImprovedIntentResolver
import fe.linksheet.experiment.improved.resolver.ResolveEvent
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.ui.PreviewTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LoadingIndicator(events: StateFlow<ResolveEvent>) {
    val event by events.collectOnIO(initialState = ResolveEvent.Initialized)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.loading_link),
            style = MaterialTheme.typography.titleMedium
        )

        Text(text = "${(event as? ResolveEvent.Message)?.message}", style = MaterialTheme.typography.bodyMedium)
//        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingIndicatorPreview() {
    val events = MutableStateFlow<ResolveEvent>(ResolveEvent.Initialized)

    LaunchedEffect(key1 = Unit) {
        var i = 0
        while (true) {
            events.emit(ResolveEvent.Message("Message $i"))
            delay(2000)
            i++
        }
    }

    PreviewTheme {
        LoadingIndicator(events = events)
    }
}
