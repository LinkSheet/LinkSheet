package fe.linksheet.extension.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.linksheet.composable.util.Searchbar
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.searchHeader(
    @StringRes subtitleId: Int,
    filter: String,
    searchFilter: MutableStateFlow<String>,
) {
    stickyHeader(key = "header") {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            PreferenceSubtitle(
                text = stringResource(subtitleId),
                paddingHorizontal = 0.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Searchbar(filter = filter, onFilterChanged = {
                searchFilter.value = it
            })

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
