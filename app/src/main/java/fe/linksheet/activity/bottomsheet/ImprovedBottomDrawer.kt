package fe.linksheet.activity.bottomsheet

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
import androidx.compose.ui.graphics.Shape
import fe.linksheet.util.AndroidVersion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedBottomDrawer(
    contentModifier: Modifier,
    landscape: Boolean = false,
    isBlackTheme: Boolean = isSystemInDarkTheme(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    hide: () -> Unit,
    sheetContent: @Composable (Modifier) -> Unit = {},
) {
    ModalBottomSheet(
        // TODO: Change to default? (surfaceContainerLow)
        containerColor = MaterialTheme.colorScheme.surface,
        shape = shape,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
        sheetState = sheetState,
//        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        windowInsets = WindowInsets(0, 0, 0, 0),
//        windowInsets = androidx.compose.material3.fix.BottomSheetDefaults.windowInsets,
//        windowInsets =  WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
//        windowInsets = if (landscape) WindowInsets.systemBars else WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
        onDismissRequest = hide
    ) {
        if (AndroidVersion.AT_LEAST_API_30_R) {
            Box(modifier = Modifier.navigationBarsPadding()) {
                sheetContent(contentModifier)
            }
        } else {
            val safeDrawing = WindowInsets.safeDrawing.asPaddingValues()

            val bottomPadding = remember { safeDrawing.calculateBottomPadding() }
            val contentModifier = with(contentModifier) {
                if (landscape) this else padding(bottom = bottomPadding)
            }

            sheetContent(contentModifier)
        }
    }
}
