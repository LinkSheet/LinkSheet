package fe.linksheet.composable.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import kotlinx.coroutines.launch


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
    content: @Composable () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = shape,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
        sheetState = drawerState,
        windowInsets = if (landscape) WindowInsets.systemBars else WindowInsets.statusBars,
        onDismissRequest = hide ?: {
            coroutineScope.launch { drawerState.hide() }
            Unit
        }
    ) {
//        Spacer(
//            modifier = Modifier
////                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
//                .fillMaxWidth()
//                .height(
//
//                    WindowInsets.statusBars
//                        .asPaddingValues()
//                        .calculateTopPadding()
//
//                )
//        )

        Column(modifier = if (!landscape) Modifier.navigationBarsPadding() else Modifier) {
            sheetContent()
        }

        // Place bottom padding manually so color is not overridden
//        Spacer(
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
//                .fillMaxWidth()
//                .height(
//
//                        WindowInsets.navigationBars
//                            .asPaddingValues()
//                            .calculateBottomPadding()
//
//                )
//        )
    }
}
