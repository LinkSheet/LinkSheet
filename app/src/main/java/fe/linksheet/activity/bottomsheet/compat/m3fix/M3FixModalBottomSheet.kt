package fe.linksheet.activity.bottomsheet

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.fix.SheetState
import androidx.compose.material3.fix.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import fe.composekit.core.AndroidVersion
import fe.linksheet.activity.bottomsheet.compat.m3fix.M3FixSheetState
import fe.linksheet.activity.bottomsheet.compat.m3fix.rememberM3FixModalBottomSheetState
import fe.linksheet.activity.bottomsheet.compat.m3fix.M3FixModalBottomSheetApi30
import fe.linksheet.activity.bottomsheet.compat.m3fix.M3FixModalBottomSheetPreApi30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3FixModalBottomSheet(
    contentModifier: Modifier,
    debug: Boolean = false,
    isBlackTheme: Boolean = isSystemInDarkTheme(),
    sheetState: M3FixSheetState = rememberM3FixModalBottomSheetState(),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    hide: () -> Unit,
    sheetContent: @Composable (Modifier) -> Unit = {},
) {
    M3FixModalBottomSheet(
        contentModifier = contentModifier,
        debug = debug,
        isBlackTheme = isBlackTheme,
        sheetState = sheetState.state,
        shape = shape,
        hide = hide,
        sheetContent = sheetContent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun M3FixModalBottomSheet(
    contentModifier: Modifier,
    debug: Boolean = false,
    isBlackTheme: Boolean = isSystemInDarkTheme(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    hide: () -> Unit,
    sheetContent: @Composable (Modifier) -> Unit = {},
) {
    if (AndroidVersion.isAtLeastApi30R()) {
        M3FixModalBottomSheetApi30(
            contentModifier = contentModifier,
            debug = debug,
            isBlackTheme = isBlackTheme,
            sheetState = sheetState,
            shape = shape,
            hide = hide,
            sheetContent = sheetContent
        )
    } else {
        M3FixModalBottomSheetPreApi30(
            contentModifier = contentModifier,
            debug = debug,
            isBlackTheme = isBlackTheme,
            sheetState = sheetState,
            shape = shape,
            hide = hide,
            sheetContent = sheetContent
        )
    }
}
