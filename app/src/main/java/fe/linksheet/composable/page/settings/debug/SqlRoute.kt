@file:OptIn(ExperimentalMaterial3Api::class)

package fe.linksheet.composable.page.settings.debug

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.feature.sql.Column
import fe.linksheet.feature.sql.SqlRow
import fe.linksheet.module.viewmodel.SqlViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SqlRoute(onBackPressed: () -> Unit, viewModel: SqlViewModel = koinViewModel()) {
    val scope = rememberCoroutineScope()
    SqlRouteInternal(
        onBackPressed = onBackPressed,
        rows = viewModel.rows,
        text = viewModel.text.value,
        runSql = { text -> viewModel.run(text) }
    )
}

@Composable
private fun SqlRouteInternal(
    onBackPressed: () -> Unit,
    rows: SnapshotStateList<SqlRow>,
    text: String?,
    runSql: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val textState = rememberTextFieldState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberScrollState()

    SaneScaffoldSettingsPage(
        headline = stringResource(id = fe.linksheet.R.string.settings__title_sql),
        onBackPressed = onBackPressed,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                onClick = { runSql(textState.text.toString()) }
            ) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null)
            }
        }
    ) {
        item(key = 0) {
            OutlinedTextField(
                state = textState,
                lineLimits = TextFieldLineLimits.MultiLine(),
                label = {
                    Text(text = stringResource(id = fe.linksheet.R.string.settings_sql__text_query))
                }
            )
        }

        item(key = 3) {
            if (text != null) {
                Text(
                    modifier = Modifier.horizontalScroll(scrollState),
                    text = text,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}


@Composable
private fun SqlRow(row: SqlRow) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for ((name, column) in row.columns) {
            Text(text = "${column.value}", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Preview
@Composable
private fun SqlRowPreview() {
    val row = SqlRow(
        mapOf("_id" to Column.IntValue(1))
    )
    SqlRow(row)
}


@Preview
@Composable
private fun SqlRoutePreview() {
    SqlRouteInternal(
        onBackPressed = {},
        rows = remember { mutableStateListOf<SqlRow>() },
        text = """| foo   | test | test2 |
|-------|------|-------|
| bar   |    0 |       |
| world | 1337 |   1.5 |""",
        runSql = { _ -> },
    )
}
