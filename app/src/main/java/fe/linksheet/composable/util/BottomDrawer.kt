package fe.linksheet.composable.util

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BottomDrawer3(
//    onDismissRequest: () -> Unit,
//    modifier: Modifier = Modifier,
//    isBlackTheme: Boolean = false,
//    sheetState: SheetState,
//    sheetContent: @Composable ColumnScope.() -> Unit,
//) {
//    val scope = rememberCoroutineScope()
//    BoxWithConstraints(
//        modifier = Modifier
//            .fillMaxSize()
//            .clickable {
////                scope.launch { drawerState.hide() }
//            },
//        contentAlignment = Alignment.BottomCenter
//    ) {
//        ModalBottomSheet(
//            onDismissRequest = onDismissRequest,
//            modifier = modifier,
//            shape = RoundedCornerShape(
//                topStart = 16.dp,
//                topEnd = 16.dp,
//                bottomEnd = 0.dp,
//                bottomStart = 0.dp
//            ),
//            sheetState = sheetState,
//            containerColor = MaterialTheme.colorScheme.surface,
//            contentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
//            tonalElevation = if (sheetState.isVisible) BottomSheetDefaults.Elevation else 0.dp,
//            scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
//            dragHandle = {},
//        ) {
//            Surface(
//                color = MaterialTheme.colorScheme.surface,
//                tonalElevation = if (isBlackTheme) 0.dp else 6.dp,
//                modifier = Modifier
//            ) {
//                Column(modifier = Modifier.navigationBarsPadding()) {
//                    sheetContent()
//                }
//            }
//        }
//    }
//}

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
        windowInsets = if(landscape) WindowInsets.systemBars else WindowInsets.statusBars,
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

        Column(modifier = if(!landscape) Modifier.navigationBarsPadding() else Modifier) {
            sheetContent()
//            Spacer(modifier = Modifier.height(28.dp))
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
