package fe.linksheet.experiment.ui.overhaul.composable.component.appbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyColumnPageDefaults
import fe.linksheet.experiment.ui.overhaul.composable.util.TextContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    titleContent: TextContent,
    placeholderContent: TextContent,
    query: String,
    onQueryChange: (String?) -> Unit,
    onBackPressed: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Column(
        modifier = Modifier.padding(bottom = SaneLazyColumnPageDefaults.BottomSpacing),
        verticalArrangement = Arrangement.spacedBy((-1).dp)
    ) {
        TopAppBar(
            title = titleContent.content,
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )

        DockedSearchBar(
            modifier = Modifier.padding(horizontal = SaneLazyColumnPageDefaults.HorizontalSpacing),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = placeholderContent.content,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange(null) }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            },
            expanded = false,
            onExpandedChange = {},
            content = {}
        )

//        var selectedIndex by remember { mutableIntStateOf(0) }
//        val options = listOf(
//            R.string.enabled to Icons.Outlined.Visibility,
//            R.string.disabled to Icons.Outlined.VisibilityOff
//        )
//
//        SingleChoiceSegmentedButtonRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = SaneLazyColumnPageDefaults.HorizontalSpacing),
//        ) {
//            options.forEachIndexed { index, (stringRes, icon) ->
//                SegmentedButton(
//                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
//                    onClick = { selectedIndex = index },
//                    icon = {
//                        Icon(imageVector = icon, contentDescription = null)
//                    },
//                    selected = index == selectedIndex
//                ) {
//                    Text(text = stringResource(id = stringRes))
//                }
//            }
//        }
    }
}
