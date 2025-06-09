package fe.linksheet.module.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fe.linksheet.feature.sql.MarkdownTable
import fe.linksheet.feature.sql.SqlRow
import fe.linksheet.feature.sql.SqlQueryFeature
import fe.linksheet.module.database.LinkSheetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch

class SqlViewModel(val database: LinkSheetDatabase) : ViewModel() {
    private val feature = SqlQueryFeature(database.openHelper)
    private val table = MarkdownTable()

    val rows = SnapshotStateList<SqlRow>()
    val text = mutableStateOf<String?>(null)

    fun run(query: String) = viewModelScope.launch(Dispatchers.IO) {
        rows.clear()
        feature.query(query)
            .buffer(capacity = Channel.UNLIMITED)
            .collect { rows.add(it) }

        text.value = table.create(rows)
    }
}

