package app.linksheet.feature.scenario.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.linksheet.feature.engine.eval.expression.IntentPackageExpression

@Composable
fun IntentPackageExpressionUi(expression: IntentPackageExpression) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(InputChipDefaults.shape),
//            .optionalClickable(onClick = {}),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
    ) {
        Column(modifier = Modifier.padding(all = 10.dp)) {
            Text(text = "Intent", textAlign = TextAlign.Start)
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item {
                    NamedInputChipParameter(name = "action:") { RootExpressionUi(expression = expression.action) }
                }
                item {
                    NamedInputChipParameter(name = "data:") { RootExpressionUi(expression = expression.data) }
                }
                item {
                    NamedInputChipParameter(name = "packageName:") { RootExpressionUi(expression = expression.packageName) }
                }
            }
        }
    }
}
