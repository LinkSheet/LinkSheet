package fe.linksheet.composable.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fe.linksheet.extension.compose.items

@Composable
fun DialogColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), content = content
    )
}

@Composable
fun DialogSpacer() {
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun <T> DialogContent(
    items: List<T>?,
    key: ((T) -> Any)? = null,
    bottomRow: @Composable RowScope.() -> Unit,
    content: @Composable LazyItemScope.(T) -> Unit
) {
    DialogBox(loading = items == null, bottomRow = bottomRow) {
        items(items = items!!, key = key, itemContent = content)
    }
}

@Composable
fun <K, V> DialogContent(
    items: Map<K, V>?,
    key: (K) -> Any,
    bottomRow: @Composable RowScope.() -> Unit,
    content: @Composable LazyItemScope.(K, V) -> Unit
) {
    DialogBox(loading = items == null, bottomRow = bottomRow) {
        items(items = items!!, key = key, itemContent = content)
    }
}

@Composable
fun DialogBox(
    loading: Boolean,
    bottomRow: @Composable RowScope.() -> Unit,
    content: LazyListScope.() -> Unit
) {
    if (loading) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
        }
        Spacer(modifier = Modifier.height(20.dp))
    } else {
        Box {
            LazyColumn(modifier = Modifier.padding(bottom = 40.dp)) {
                content()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(40.dp),
                horizontalArrangement = Arrangement.End,
                content = bottomRow
            )
        }
    }
}