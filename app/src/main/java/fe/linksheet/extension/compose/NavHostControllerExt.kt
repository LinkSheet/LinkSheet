package fe.linksheet.extension.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun NavHostController.ObserveDestination(onNavigate: NavController.OnDestinationChangedListener) {
    DisposableEffect(key1 = this@ObserveDestination) {
        addOnDestinationChangedListener(onNavigate)
        onDispose {
            removeOnDestinationChangedListener(onNavigate)
        }
    }
}
