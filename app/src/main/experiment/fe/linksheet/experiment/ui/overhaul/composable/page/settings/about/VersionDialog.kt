package fe.linksheet.experiment.ui.overhaul.composable.page.settings.about

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import fe.linksheet.BuildConfig
import fe.linksheet.experiment.ui.overhaul.composable.component.dialog.AlertDialogContent
import fe.linksheet.extension.android.toImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VersionDialog(
    dismiss: () -> Unit,
    confirm: () -> Unit
) {
    val context = LocalContext.current
    val icon = remember {
        context.packageManager.getApplicationIcon(BuildConfig.APPLICATION_ID).toImageBitmap()
    }

    BasicAlertDialog(
        onDismissRequest = dismiss,
        modifier = Modifier,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        AlertDialogContent(
            buttons = {},

//            shape = shape,
//            containerColor = containerColor,
//            tonalElevation = tonalElevation,
            // Note that a button content color is provided here from the dialog's token, but in
            // most cases, TextButtons should be used for dismiss and confirm buttons.
            // TextButtons will not consume this provided content color value, and will used their
            // own defined or default colors.
//            buttonContentColor = MaterialTheme.colorScheme.primary,
//            iconContentColor = iconContentColor,
//            titleContentColor = titleContentColor,
//            textContentColor = textContentColor,
        )
    }


//    R.drawable.app_linksheet
}
