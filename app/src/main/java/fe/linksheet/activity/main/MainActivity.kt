package fe.linksheet.activity.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.collection.valueIterator
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.activity.util.DebugStatePublisher
import fe.linksheet.activity.util.NavGraphDebugState
import fe.linksheet.activity.util.UiEvent
import fe.linksheet.activity.UiEventReceiverBaseComponentActivity
import fe.linksheet.composable.page.settings.privacy.analytics.rememberAnalyticDialog
import fe.linksheet.composable.page.settings.privacy.remoteconfig.rememberRemoteConfigDialog
import fe.linksheet.composable.ui.BoxAppHost
import fe.linksheet.extension.compose.AddIntentDeepLinkHandler
import fe.linksheet.extension.compose.ObserveDestination
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.util.buildconfig.Build
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : UiEventReceiverBaseComponentActivity() {
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            BoxAppHost {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()

                val uiEvent by events.collectAsStateWithLifecycle()
                LaunchedEffect(key1 = uiEvent) {
                    uiEvent?.let {
                        when (it) {
                            is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(message = it.text)
                            is UiEvent.NavigateTo -> navController.navigate(it.route)
                        }
                    }
                }

                AddIntentDeepLinkHandler(navController = navController)

                val remoteConfigDialogDismissed by viewModel.remoteConfigDialogDismissed.collectAsStateWithLifecycle(
                    // Assume true to avoid having to show, then quickly dismiss the dialog, once the actual state is emitted to the flow
                    initialValue = true
                )
                val remoteConfigDialog = rememberRemoteConfigDialog(
                    onChanged = { viewModel.setRemoteConfig(it) }
                )

                LaunchedEffect(key1 = remoteConfigDialogDismissed) {
                    if (!remoteConfigDialogDismissed) {
                        remoteConfigDialog.open()
                    }
                }

                if (Build.IsDebug) {
                    navController.ObserveDestination { _, destination, args ->
                        viewModel.enqueueNavEvent(destination, args)
                    }

                    val telemetryLevel by viewModel.telemetryLevel.collectAsStateWithLifecycle()
                    val telemetryShowInfoDialog by viewModel.telemetryShowInfoDialog.collectAsStateWithLifecycle()

                    val analyticsDialog = rememberAnalyticDialog(
                        telemetryLevel = telemetryLevel,
                        onChanged = { viewModel.updateTelemetryLevel(it) }
                    )

                    LaunchedEffect(key1 = Unit) {
                        if (telemetryShowInfoDialog) {
                            analyticsDialog.open()
                        }
                    }
                }

                MainNavHost(
                    navController = navController,
                    navigate = { navController.navigate(it) },
                    navigateNew = { navController.navigate(it) },
                    onBackPressed = { navController.popBackStack() }
                )

                if (Build.IsDebug) {
                    LaunchedEffect(key1 = Unit) {
                        @SuppressLint("RestrictedApi")
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
