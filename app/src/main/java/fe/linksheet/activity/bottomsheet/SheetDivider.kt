package fe.linksheet.activity.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SheetDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp),
        color = MaterialTheme.colorScheme.outline.copy(0.25f)
    )
}

@Composable
@Preview(showBackground = true)
private fun SheetDividerPreview() {
    Column(modifier = Modifier.size(width = 100.dp, height = 50.dp)) {
        SheetDivider()
    }
}
