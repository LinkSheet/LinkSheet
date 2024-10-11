package fe.linksheet.composable.page.settings.debug.log

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.kotlin.extension.unixMillisUtc
import fe.kotlin.extension.time.localizedString
import fe.linksheet.R


@Composable
fun LogCard(logEntry: PrefixMessageCardContent, border: BorderStroke = CardDefaults.outlinedCardBorder()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        SelectionContainer {
            OutlinedCard(shape = RoundedCornerShape(12.dp), border = border) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = logEntry.prefix ?: stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = logEntry.start.unixMillisUtc.value.localizedString(),
                        style = MaterialTheme.typography.labelSmall
                    )

                    for (message in logEntry.messages) {
                        Text(text = message, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}
