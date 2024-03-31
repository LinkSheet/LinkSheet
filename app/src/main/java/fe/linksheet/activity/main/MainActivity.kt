package fe.linksheet.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.util.Consumer
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fe.linksheet.*
import fe.linksheet.activity.UiEvent
import fe.linksheet.activity.UiEventReceiverBaseComponentActivity
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.BoxAppHost
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : UiEventReceiverBaseComponentActivity() {
    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            BoxAppHost(modifier = Modifier.safeContentPadding()) {
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(key1 = eventState.value) {
                    if (eventState.value is UiEvent.ShowSnackbar) {
                        snackbarHostState.showSnackbar(message = (eventState.value as UiEvent.ShowSnackbar).text)
                    }
                }

                val navController = rememberNavController()

                RegisterIntentHandler(navController = navController)

                RegisterDestinationObserver(navController) { _, destination, args ->
                    mainViewModel.enqueueNavigateEvent(destination, args)
                }

                MainNavHost(
                    navController = navController,
                    // TODO: Can this be moved inside to improve performance? (defer state reads as long as possible)
                    uiOverhaul = mainViewModel.uiOverhaul()
                ) { navController.popBackStack() }

                SnackbarHost(modifier = Modifier.align(Alignment.BottomCenter), hostState = snackbarHostState)
            }
        }
    }

    @Composable
    private fun RegisterDestinationObserver(
        navController: NavHostController,
        onNavigate: NavController.OnDestinationChangedListener,
    ) {
        DisposableEffect(key1 = navController) {
            navController.addOnDestinationChangedListener(onNavigate)
            onDispose {
                navController.removeOnDestinationChangedListener(onNavigate)
            }
        }
    }

    @Composable
    private fun RegisterIntentHandler(navController: NavHostController) {
        DisposableEffect(key1 = navController) {
            val consumer = Consumer<Intent> { navController.handleDeepLink(it) }
            this@MainActivity.addOnNewIntentListener(consumer)
            onDispose {
                this@MainActivity.removeOnNewIntentListener(consumer)
            }
        }
    }
}
