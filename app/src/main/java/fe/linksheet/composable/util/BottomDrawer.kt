package fe.linksheet.composable.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.FlexibleSheetState
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
) {
    val coroutineScope = rememberCoroutineScope()
    val safeDrawing = WindowInsets.safeDrawing.asPaddingValues()

    ModalBottomSheet(
        modifier = modifier,
        // TODO: Change to default? (surfaceContainerLow)
        containerColor = MaterialTheme.colorScheme.surface,
        shape = shape,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
        sheetState = drawerState,
        windowInsets = WindowInsets(0, 0, 0, 0),
//        windowInsets =  WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
//        windowInsets = if (landscape) WindowInsets.systemBars else WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
        onDismissRequest = hide ?: {
            coroutineScope.launch { drawerState.hide() }
            Unit
        }
    ) {
        // Works on both API <29 and API 30+
        val bottomPadding = remember { safeDrawing.calculateBottomPadding() }

        Column(modifier = if (landscape) Modifier else Modifier.padding(bottom = bottomPadding)) {
            sheetContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlexibleBottomDrawer(
    modifier: Modifier = Modifier,
    landscape: Boolean = false,
    isBlackTheme: Boolean = isSystemInDarkTheme(),
//    sheetState: FlexibleSheetState = rememberFlexibleBottomSheetState(),
    drawerState: FlexibleSheetState,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    hide: (() -> Unit)? = null,
    sheetContent: @Composable ColumnScope.() -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    FlexibleBottomSheet(
        containerColor = MaterialTheme.colorScheme.surface,
        shape = shape,
        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
        sheetState = drawerState,
        windowInsets = if (landscape) WindowInsets.systemBars else WindowInsets.statusBars,
        onDismissRequest = hide ?: {
            coroutineScope.launch { drawerState.hide() }
            Unit
        },
    ) {
        Column(modifier = if (!landscape) Modifier.navigationBarsPadding() else Modifier) {
            sheetContent()
        }
//        Text(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            text = "This is Flexible Bottom Sheet",
//            textAlign = TextAlign.Center,
//            color = Color.White,
//        )
    }


//    ModalBottomSheet(
//        modifier = modifier,
//        // TODO: Change to default? (surfaceContainerLow)
//        containerColor = MaterialTheme.colorScheme.surface,
//        shape = shape,
//        scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
//        sheetState = drawerState,
////        windowInsets = WindowInsets(0),
////        windowInsets = WindowInsets.systemBars,
//        windowInsets = if (landscape) WindowInsets.systemBars else WindowInsets.statusBars,
//        onDismissRequest = hide ?: {
//            coroutineScope.launch { drawerState.hide() }
//            Unit
//        }
//    ) {
//        Column(modifier = if (!landscape) Modifier.navigationBarsPadding() else Modifier) {
//            sheetContent()
//        }
//    }
}
