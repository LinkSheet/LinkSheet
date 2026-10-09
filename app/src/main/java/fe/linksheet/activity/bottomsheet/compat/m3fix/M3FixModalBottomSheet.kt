package fe.linksheet.activity.bottomsheet.compat.m3fix

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.fix.SheetState
import androidx.compose.material3.fix.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import app.linksheet.api.BOTTOM_SHEET_TEST_TAG
import fe.composekit.core.AndroidVersion


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
        modifier = Modifier
            .semantics { testTagsAsResourceId = true }
            .testTag(BOTTOM_SHEET_TEST_TAG),
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
    modifier: Modifier = Modifier,
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
            modifier = modifier,
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
            modifier = modifier,
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
