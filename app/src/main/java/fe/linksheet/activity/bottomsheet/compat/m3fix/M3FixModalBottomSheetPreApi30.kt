package fe.linksheet.activity.bottomsheet.compat.m3fix

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.fix.ModalBottomSheet
import androidx.compose.material3.fix.SheetState
import androidx.compose.material3.fix.SheetValue
import androidx.compose.material3.fix.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.composable.util.debugBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun M3FixModalBottomSheetPreApi30(
    contentModifier: Modifier,
    debug: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    isBlackTheme: Boolean = isSystemInDarkTheme(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    hide: () -> Unit,
    sheetContent: @Composable (Modifier) -> Unit = {},
) {
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val horizontalMargin = 56.dp
    val targetWidth = BottomSheetDefaults.SheetMaxWidth
    val width = LocalConfiguration.current.screenWidthDp.dp - targetWidth
    val insetHorizontal = width / 2 + horizontalMargin

    val safeDrawing = WindowInsets.safeDrawing.asPaddingValues()
    ModalBottomSheet(
        modifier = Modifier.debugBorder(debug, 1.dp, Color.Cyan),
//        sheetMaxWidth = Dp.Unspecified,
        sheetMaxWidth = targetWidth,
        containerColor = containerColor,
        shape = shape,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
        sheetState = sheetState,
        windowInsets = WindowInsets(insetHorizontal, 0.dp, 0.dp, 0.dp),
        onDismissRequest = hide
    ) {
            if (landscape) {
                sheetContent(contentModifier)
            } else {
                val bottomPadding = remember { safeDrawing.calculateBottomPadding() }
                sheetContent(contentModifier.padding(bottom = bottomPadding))
            }
    }
}

@Preview(showSystemUi = true, showBackground = true, device = Devices.AUTOMOTIVE_1024p, apiLevel = 29)
@Preview(showSystemUi = true, showBackground = true, device = Devices.PIXEL_3, apiLevel = 29)
private annotation class OrientationPreviewsPreAPI30

@OptIn(ExperimentalMaterial3Api::class)
@OrientationPreviewsPreAPI30
@Composable
private fun M3FixModalBottomSheetPreApi30Preview() {
    val state = rememberModalBottomSheetState(initialValue = SheetValue.Expanded)
    M3FixModalBottomSheetPreApi30(
        contentModifier = Modifier,
        debug = true,
        containerColor = Color.Red,
        sheetState = state,
        hide = {},
        sheetContent = {
            Box(
                modifier = Modifier
                    .border(1.dp, Color.Blue)
            ) {
                Text(text = "Outer box")
            }
        }
    )
}
