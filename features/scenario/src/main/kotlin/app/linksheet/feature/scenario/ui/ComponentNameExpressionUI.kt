package app.linksheet.feature.scenario.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.linksheet.feature.engine.eval.expression.ComponentNameExpression

@Composable
fun ComponentNameExpressionUi(expression: ComponentNameExpression) {
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
            Text(text = "ComponentName", textAlign = TextAlign.Start)
            Column(
//                contentPadding = PaddingValues(horizontal = 6.dp),
                modifier = Modifier.padding(horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
//                item {
                NamedInputChipParameter(name = "pkg:") { RootExpressionUi(expression = expression.pkg) }
//                }
//                item {
                NamedInputChipParameter(name = "cls:") { RootExpressionUi(expression = expression.cls) }
//                }
            }
        }
    }
}

@Composable
fun ComponentNameExpressionUi2(expression: ComponentNameExpression) {
    LazyRow(verticalAlignment = Alignment.CenterVertically) {
        item {
            Text(text = "{")
        }
        item {
            NamedInputChipParameter(name = "pkg:") { RootExpressionUi(expression = expression.pkg) }
        }
        item {
            NamedInputChipParameter(name = "cls:") { RootExpressionUi(expression = expression.cls) }
        }
        item {
            Text(text = "}")
        }
    }
}
