package fe.linksheet.activity.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.fix.ModalBottomSheet
import androidx.compose.material3.fix.SheetState
import androidx.compose.material3.fix.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedBottomDrawer(
    contentModifier: Modifier = Modifier,
    landscape: Boolean = false,
    isBlackTheme: Boolean = isSystemInDarkTheme(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    hide: () -> Unit,
    sheetContent: @Composable ColumnScope.() -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val safeDrawing = WindowInsets.safeDrawing.asPaddingValues()

    ModalBottomSheet(
//        modifier = modifier,
        // TODO: Change to default? (surfaceContainerLow)
        containerColor = MaterialTheme.colorScheme.surface,
        shape = shape,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0),
//        windowInsets =  WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
//        windowInsets = if (landscape) WindowInsets.systemBars else WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
        onDismissRequest = hide
    ) {
        // Works on both API <29 and API 30+
        val bottomPadding = remember { safeDrawing.calculateBottomPadding() }

        Column(modifier = if (landscape) contentModifier else contentModifier.padding(bottom = bottomPadding)) {
            sheetContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, device = Devices.PIXEL_7A)
@Composable
private fun ImprovedBottomDrawerPreview() {
    val safeDrawing = WindowInsets.safeDrawing.asPaddingValues()
    val bottomPadding = remember { safeDrawing.calculateBottomPadding() }

    Column(modifier = Modifier.padding(bottom = bottomPadding)) {
        Box(modifier = Modifier.height(24.dp).fillMaxWidth().background(Color.Red)) {

        }
    }
}
