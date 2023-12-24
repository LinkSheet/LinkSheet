package fe.linksheet.composable.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomDrawer(
    modifier: Modifier = Modifier,
    isBlackTheme: Boolean = false,
    drawerState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    sheetContent: @Composable ColumnScope.() -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(onDismissRequest = {
        coroutineScope.launch {
            drawerState.hide()

        }
    }) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            sheetContent()
        }
    }
}