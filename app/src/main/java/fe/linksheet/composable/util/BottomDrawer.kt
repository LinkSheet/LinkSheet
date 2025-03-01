package fe.linksheet.composable.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import fe.android.version.AndroidVersion
import kotlinx.coroutines.launch


private val noWindowInsets = WindowInsets(0, 0, 0, 0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomDrawer(
    modifier: Modifier = Modifier,
    landscape: Boolean = false,
    isBlackTheme: Boolean = isSystemInDarkTheme(),
    drawerState: SheetState = rememberModalBottomSheetState(),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    hide: (() -> Unit)? = null,
    sheetContent: @Composable ColumnScope.() -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = modifier,
        // TODO: Change to default? (surfaceContainerLow)
        containerColor = MaterialTheme.colorScheme.surface,
        shape = shape,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
        sheetState = drawerState,
        contentWindowInsets = {
            if (AndroidVersion.isAtLeastApi30R()) noWindowInsets else WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical)
        },
        onDismissRequest = hide ?: {
            coroutineScope.launch { drawerState.hide() }
            Unit
        }
    ) {
        if (AndroidVersion.isAtLeastApi30R()) {
            Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars), content = sheetContent)
        } else {
            sheetContent()
        }
    }
}
