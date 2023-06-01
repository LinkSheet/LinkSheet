package fe.linksheet.composable.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R

@Composable
fun Searchbar(
    filter: String,
    onFilterChanged: (String) -> Unit,
) {
    Searchbar(
        filter = filter,
        onValueChange = onFilterChanged,
        onClearClick = { onFilterChanged("") }
    )
}

@Composable
fun Searchbar(
    filter: String,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        placeholder = { Text(text = stringResource(id = R.string.search)) },
        shape = RoundedCornerShape(size = 32.dp),
        value = filter,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search),
            )
        },
        trailingIcon = {
            if (filter.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.clear),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        onValueChange = onValueChange
    )
}