package fe.linksheet.exp

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import fe.androidx.compose.material3.SearchBar
import fe.androidx.compose.material3.SearchBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarBox(
    modifier: Modifier = Modifier
) {
    val textFieldState = rememberTextFieldState()
    var expanded by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        modifier = modifier
//            .align(Alignment.TopCenter)
            .semantics { traversalIndex = 0f },
        inputField = {
            SearchBarDefaults.InputField(
                state = textFieldState,
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("Hinted search text") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it },
        content = {}
    )
//    {
////        Column(Modifier.verticalScroll(rememberScrollState())) {
////            repeat(4) { idx ->
////                val resultText = "Suggestion $idx"
////                ListItem(
////                    headlineContent = { Text(resultText) },
////                    supportingContent = { Text("Additional info") },
////                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
////                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
////                    modifier =
////                        Modifier
////                            .clickable {
////                                textFieldState.setTextAndPlaceCursorAtEnd(resultText)
////                                expanded = false
////                            }
////                            .fillMaxWidth()
////                            .padding(horizontal = 16.dp, vertical = 4.dp)
////                )
////            }
////        }
//    }

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .semantics { isTraversalGroup = true }
//    ) {
//
//
////        LazyColumn(
////            contentPadding = PaddingValues(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
////            verticalArrangement = Arrangement.spacedBy(8.dp),
////            modifier = Modifier.semantics { traversalIndex = 1f },
////        ) {
////            val list = List(100) { "Text $it" }
////            items(count = list.size) {
////                Text(
////                    text = list[it],
////                    modifier = Modifier
////                        .fillMaxWidth()
////                        .padding(horizontal = 16.dp),
////                )
////            }
////        }
//    }
}
