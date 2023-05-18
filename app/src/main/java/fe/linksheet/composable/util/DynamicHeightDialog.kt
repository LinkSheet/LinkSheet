package fe.linksheet.composable.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import fe.linksheet.extension.runIf


// androidx.compose.material3.AlertDialog
val DialogMinWidth = 280.dp
val DialogMaxWidth = 560.dp

@Composable
fun Dialog(
    dynamicHeight: Boolean = false,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    val resources = LocalContext.current.resources
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.runIf(dynamicHeight,
                    runIf = {
                        it.fillMaxHeight().clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismissRequest,
                        )
                    },
                    runElse = {
                        it.sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth)
                    }
                ).then(Modifier.semantics {
                    // androidx.compose.material3.AndroidAlertDialog.android.kt
                    paneTitle = resources.getString(androidx.compose.material3.R.string.dialog)
                }
            ),
            propagateMinConstraints = !dynamicHeight
        ) {
            Surface(shape = MaterialTheme.shapes.large) {
                content()
            }
        }
    }
}