package fe.linksheet.activity.main

import android.os.Bundle
import androidx.collection.valueIterator
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import fe.linksheet.activity.DebugStatePublisher
import fe.linksheet.activity.NavGraphDebugState
import fe.linksheet.activity.UiEvent
import fe.linksheet.activity.UiEventReceiverBaseComponentActivity
import fe.linksheet.composable.page.settings.privacy.analytics.rememberAnalyticDialog
import fe.linksheet.extension.compose.AddIntentDeepLinkHandler
import fe.linksheet.extension.compose.ObserveDestination
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.composable.ui.BoxAppHost
import fe.linksheet.util.buildconfig.BuildType
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : UiEventReceiverBaseComponentActivity() {
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            BoxAppHost {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()

                LaunchedEffect(key1 = eventState.value) {
                    if (eventState.value is UiEvent.ShowSnackbar) {
                        snackbarHostState.showSnackbar(message = (eventState.value as UiEvent.ShowSnackbar).text)
                    } else if (eventState.value is UiEvent.NavigateTo) {
                        navController.navigate((eventState.value as UiEvent.NavigateTo).route)
                    }
                }

                AddIntentDeepLinkHandler(navController = navController)

                if (BuildType.current.allowDebug) {
                    navController.ObserveDestination { _, destination, args ->
                        viewModel.enqueueNavEvent(destination, args)
                    }

                    val analyticsDialog = rememberAnalyticDialog(
                        telemetryLevel = viewModel.telemetryLevel(),
                        onChanged = { viewModel.updateTelemetryLevel(it) }
                    )

                    LaunchedEffect(key1 = Unit) {
                        if (viewModel.telemetryShowInfoDialog()) {
                            analyticsDialog.open()
                        }
                    }
                }

                MainNavHost(
                    navController = navController,
                    navigate = { navController.navigate(it) },
                    onBackPressed = { navController.popBackStack() }
                )

                if (BuildType.current.allowDebug) {
                    LaunchedEffect(key1 = Unit) {
                        val graphNodes = navController.graph.nodes.valueIterator().asSequence().toList()
                        DebugStatePublisher.publishDebugState(NavGraphDebugState(graphNodes))
                    }
                }

                SnackbarHost(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding(),
                    hostState = snackbarHostState
                )
            }
        }
    }
}
