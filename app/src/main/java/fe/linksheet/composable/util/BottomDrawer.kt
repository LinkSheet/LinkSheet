package fe.linksheet.composable.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// TODO: This should be replaced with the material3 version above, but somehow in the new version
//  the bottomsheet does not change the color of the system navbar for some reason
@OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun BottomDrawer(
    modifier: Modifier = Modifier,
    isBlackTheme: Boolean = false,
    drawerState: androidx.compose.material.ModalBottomSheetState = androidx.compose.material.rememberModalBottomSheetState(
        androidx.compose.material.ModalBottomSheetValue.Hidden
    ),
    sheetContent: @Composable ColumnScope.() -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                scope.launch { drawerState.hide() }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        androidx.compose.material.ModalBottomSheetLayout(
            modifier = modifier,
            sheetShape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            ),
            sheetState = drawerState,
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
            sheetContentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
            sheetElevation = if (drawerState.isVisible) androidx.compose.material.ModalBottomSheetDefaults.Elevation else 0.dp,
            scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
            sheetContent = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = if (isBlackTheme) 0.dp else 6.dp,
                    modifier = Modifier
                ) {
                    Column(modifier = Modifier.navigationBarsPadding()) {
                        sheetContent()
                    }
                }
            },
            content = content,
        )
    }
}