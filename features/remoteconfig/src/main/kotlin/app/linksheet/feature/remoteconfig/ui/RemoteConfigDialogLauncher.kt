package app.linksheet.feature.remoteconfig.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import app.linksheet.feature.remoteconfig.usecase.RemoteConfigUseCase
import fe.android.preference.helper.Preference
import fe.composekit.preference.ViewModelStatePreference
import fe.composekit.preference.collectAsStateWithLifecycle

@Composable
fun RemoteConfigDialogLauncher(useCase: RemoteConfigUseCase) {
    RemoteConfigDialogLauncherInternal(
        statePreference = useCase.dialogDismissed,
        onChanged = useCase::update
    )
}

@Composable
private fun RemoteConfigDialogLauncherInternal(
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
    onChanged: (Boolean) -> Unit
) {
    val remoteConfigDialogDismissed by statePreference.collectAsStateWithLifecycle(
        // Assume true to avoid having to show, then quickly dismiss the dialog, once the actual state is emitted to the flow
        initialValue = true
    )
    val remoteConfigDialog = rememberRemoteConfigDialog(
        onChanged = onChanged
    )

    LaunchedEffect(key1 = remoteConfigDialogDismissed) {
        if (!remoteConfigDialogDismissed) {
            remoteConfigDialog.open()
        }
    }
}
