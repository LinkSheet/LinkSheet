package fe.linksheet.experiment.improved.resolver.activity.bottomsheet

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.experiment.improved.resolver.ImprovedIntentResolver
import fe.linksheet.experiment.improved.resolver.ResolveEvent
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun LoadingIndicator(resolver: ImprovedIntentResolver) {
    val event by resolver.events.collectOnIO(initialState = ResolveEvent.Initialized)
    LaunchedEffect(key1 = event) {
        Log.d("BottomSheet", "Received event: $event")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.loading_link),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold
        )

        Text(text = "${(event as? ResolveEvent.Message)?.message}")
//        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp))
    }
}



