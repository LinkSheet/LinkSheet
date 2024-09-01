package fe.linksheet.composable.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SettingEnabledCardColumn(
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    headline: String,
    subtitle: String? = null,
    subtitleBuilder: @Composable ((enabled: Boolean) -> Unit)? = buildEnabledSubtitle(subtitle = subtitle),
    contentTitle: String? = null,
) {
    SettingEnabledCardColumnCommon(contentTitle = contentTitle) {
        SwitchRow(
            checked = checked,
            onChange = onChange,
            headline = headline,
            subtitle = subtitle,
            subtitleBuilder = subtitleBuilder,
        )
    }
}


@Composable
fun SettingEnabledCardColumnCommon(
    contentTitle: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }

        if (contentTitle != null) {
            Spacer(modifier = Modifier.height(10.dp))
            SettingSpacerText(contentTitle = contentTitle)
        }
    }
}
