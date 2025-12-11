package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewContainer
import fe.linksheet.module.viewmodel.common.applist.FilterState
import fe.linksheet.module.viewmodel.common.applist.SortByState
import fe.linksheet.module.viewmodel.common.applist.SortType
import fe.linksheet.module.viewmodel.common.applist.StateModeFilter
import fe.linksheet.module.viewmodel.common.applist.TypeFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterSortSheet(
    sortState: SortByState,
    filterState: FilterState,
    onDismiss: (SortByState, FilterState) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var sortState by remember { mutableStateOf(sortState) }
    var filterState by remember { mutableStateOf(filterState) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss(sortState, filterState) },
        sheetState = sheetState
    ) {
        SheetContent(
            sortState = sortState,
            filterState = filterState,
            onSortStateChange = { sortState = it },
            onFilterState = { filterState = it },
//            onDismiss = {
//                scope.launch { sheetState.hide() }
//                    .invokeOnCompletion { if (!sheetState.isVisible) onDismiss() }
//            }
        )
    }
}

@Composable
private fun SheetContent(
    sortState: SortByState,
    filterState: FilterState,
    onSortStateChange: (SortByState) -> Unit,
    onFilterState: (FilterState) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 0.dp),
        //        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SortByColumn(
            state = sortState,
            onChange = onSortStateChange
        )

        FilterColumn(
            state = filterState,
            onChange = onFilterState
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun SheetContentPreview() {
    PreviewContainer {
        SheetContent(
            sortState = SortByState(SortType.AZ, true),
            filterState = FilterState(StateModeFilter.ShowAll, TypeFilter.All, true) ,
            onSortStateChange = {

            },
            onFilterState = {

            }
        )
    }
}
