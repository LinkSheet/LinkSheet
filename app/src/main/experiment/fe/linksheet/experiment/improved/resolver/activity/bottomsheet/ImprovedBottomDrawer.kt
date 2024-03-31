package fe.linksheet.experiment.improved.resolver.activity.bottomsheet

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import fe.linksheet.experiment.improved.resolver.material3.SheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedBottomDrawer(
    modifier: Modifier = Modifier,
    landscape: Boolean = false,
    isBlackTheme: Boolean = isSystemInDarkTheme(),
    drawerState: SheetState = fe.linksheet.experiment.improved.resolver.material3.rememberModalBottomSheetState(),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    hide: () -> Unit,
    sheetContent: @Composable ColumnScope.() -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val safeDrawing = WindowInsets.safeDrawing.asPaddingValues()

    fe.linksheet.experiment.improved.resolver.material3.ModalBottomSheet(
        modifier = modifier,
        // TODO: Change to default? (surfaceContainerLow)
        containerColor = MaterialTheme.colorScheme.surface,
        shape = shape,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
        sheetState = drawerState,
        windowInsets = WindowInsets(0, 0, 0, 0),
//        windowInsets =  WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
//        windowInsets = if (landscape) WindowInsets.systemBars else WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
        onDismissRequest = hide
    ) {
        // Works on both API <29 and API 30+
        val bottomPadding = remember { safeDrawing.calculateBottomPadding() }

        Column(modifier = if (landscape) Modifier else Modifier.padding(bottom = bottomPadding)) {
            sheetContent()
        }
    }
}
