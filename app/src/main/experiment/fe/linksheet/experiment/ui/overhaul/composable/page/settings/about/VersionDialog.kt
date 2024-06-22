package fe.linksheet.experiment.ui.overhaul.composable.page.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.dialog.AlertDialogContent
import fe.linksheet.experiment.ui.overhaul.composable.component.dialog.DialogDefaults
import fe.linksheet.experiment.ui.overhaul.composable.util.DrawableIconType.Companion.drawable
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.ui.HkGroteskFontFamily

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
