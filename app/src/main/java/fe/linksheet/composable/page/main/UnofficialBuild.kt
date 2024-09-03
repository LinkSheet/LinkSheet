package fe.linksheet.composable.page.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.ui.NewTypography

@Composable
fun UnofficialBuild() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            ColoredIcon(
                icon = Icons.Default.Warning,
                descriptionId = R.string.warning,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(id = R.string.running_unofficial_build),
                    style = NewTypography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.built_by_error),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
