package fe.linksheet.activity.bottomsheet.impl.m3fix

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.fix.ModalBottomSheet
import androidx.compose.material3.fix.SheetState
import androidx.compose.material3.fix.SheetValue
import androidx.compose.material3.fix.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.composable.util.debugBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun M3FixModalBottomSheetApi30(
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
    val targetWidth = BottomSheetDefaults.SheetMaxWidth
    ModalBottomSheet(
        modifier = Modifier.debugBorder(debug, 1.dp, Color.Cyan),
        sheetMaxWidth = targetWidth,
        containerColor = containerColor,
        shape = shape,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
        sheetState = sheetState,
//        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
//        windowInsets = androidx.compose.material3.fix.BottomSheetDefaults.windowInsets,
//        windowInsets =  WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
//        windowInsets = if (landscape) WindowInsets.systemBars else WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
        onDismissRequest = hide
    ) {
        if(landscape) {
            sheetContent(contentModifier)
        } else {
            Box(modifier = Modifier.navigationBarsPadding()) {
                sheetContent(contentModifier)
            }
        }
    }
}
@Preview(showSystemUi = true, showBackground = true, device = Devices.AUTOMOTIVE_1024p, apiLevel = 35)
@Preview(showSystemUi = true, showBackground = true, device = Devices.PIXEL_7, apiLevel = 35)
private annotation class OrientationPreviewsApi30

@OptIn(ExperimentalMaterial3Api::class)
@OrientationPreviewsApi30
@Composable
private fun M3FixModalBottomSheetApi30Preview() {
    val state = rememberModalBottomSheetState(initialValue = SheetValue.Expanded)
    M3FixModalBottomSheetApi30(
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
