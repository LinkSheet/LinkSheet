package com.junkfood.seal.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomDrawer(
    modifier: Modifier = Modifier,
    drawerState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
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
        ModalBottomSheetLayout(
            modifier = modifier,
            sheetShape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            ),
            sheetState = drawerState,
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
            sheetElevation = if (drawerState.isVisible) ModalBottomSheetDefaults.Elevation else 0.dp,
            scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
            sheetContent = {
                Column {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(5.dp))
                            sheetContent()
                            Spacer(modifier = Modifier.height(28.dp))
                        }
                    }
                }
            },
            content = content,
        )
    }
}