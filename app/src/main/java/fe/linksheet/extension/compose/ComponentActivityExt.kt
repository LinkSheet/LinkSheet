package fe.linksheet.extension.compose

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.util.Consumer
import androidx.navigation.NavHostController

@Composable
fun ComponentActivity.AddIntentDeepLinkHandler(navController: NavHostController) {
    DisposableEffect(key1 = navController) {
        val consumer = Consumer<Intent> { navController.handleDeepLink(it) }

        addOnNewIntentListener(consumer)
        onDispose {
            removeOnNewIntentListener(consumer)
        }
    }
}
