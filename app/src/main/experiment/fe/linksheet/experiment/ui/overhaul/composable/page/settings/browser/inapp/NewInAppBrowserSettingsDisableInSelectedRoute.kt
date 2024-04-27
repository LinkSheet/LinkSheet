package fe.linksheet.experiment.ui.overhaul.composable.page.settings.browser.inapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneSettingsScaffold
import fe.linksheet.extension.compose.clickable
import fe.linksheet.module.viewmodel.InAppBrowserSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewInAppBrowserSettingsDisableInSelectedRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: InAppBrowserSettingsViewModel = koinViewModel(),
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    SaneSettingsScaffold(
        headline = stringResource(id = R.string.disable_in_selected),
        onBackPressed = onBackPressed
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = -1f },
                query = text,
                onQueryChange = { text = it },
                onSearch = { active = false },
                active = active,
                onActiveChange = {
                    active = it
                },
                placeholder = { Text("Hinted search text") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            ) {}
        }
//        divider(stringRes = R.string.)


//        SearchBar(
//            modifier = Modifier
////                .align(Alignment.TopCenter)
////                .semantics { traversalIndex = -1f }
//                ,
//            query = text,
//            onQueryChange = { text = it },
//            onSearch = { active = false },
//            active = active,
//            onActiveChange = {
//                active = it
//            },
//            placeholder = { Text("Hinted search text") },
//            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
//            trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
//        ) {
//
//        }
    }


//    SearchBarSample()
//    SaneScaffoldSettingsPage(
//        headline = stringResource(id = R.string.disable_in_selected),
//        onBackPressed = onBackPressed
//    ) {
//
//    }
//
//    BrowserCommonPackageSelectorRoute(
//        headlineId = R.string.disable_in_selected,
//        subtitleId = R.string.disable_in_selected_explainer,
//        noItemsId = R.string.no_apps_found,
//        navController = navController,
//        viewModel = viewModel
//    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarSample() {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = -1f },
            query = text,
            onQueryChange = { text = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = {
                active = it
            },
            placeholder = { Text("Hinted search text") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
        ) {
            repeat(4) { idx ->
                val resultText = "Suggestion $idx"
                ListItem(
                    headlineContent = { Text(resultText) },
                    supportingContent = { Text("Additional info") },
                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                    modifier = Modifier
                        .clickable {
                            text = resultText
                            active = false
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val list = List(100) { "Text $it" }
            items(count = list.size) {
                Text(
                    list[it],
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}


