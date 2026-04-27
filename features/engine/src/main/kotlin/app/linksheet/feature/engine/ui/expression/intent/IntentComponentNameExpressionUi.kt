package app.linksheet.feature.engine.ui.expression.intent

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.engine.R
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.ComponentNameExpression
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.eval.expression.IntentComponentNameExpression
import app.linksheet.feature.engine.eval.expression.UrlToAndroidUriExpression
import app.linksheet.feature.engine.ui.ExpressionInputChip
import app.linksheet.feature.engine.ui.expression.RootExpressionUi

@Composable
internal fun IntentComponentNameExpressionUi(expression: IntentComponentNameExpression) {
    Card(
        modifier = Modifier
            .clip(InputChipDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
    ) {
//        Box(
//            modifier = Modifier
//                .padding(all = 8.dp)
//                .dashedBorder(
//                    text = "Intent",
//                    color = MaterialTheme.colorScheme.primary,
//                    backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
//                    shape = InputChipDefaults.shape
//                )
//        ) {
        Column(modifier = Modifier.padding(all = 8.dp)) {
            LazyColumn(
//                contentPadding = PaddingValues(horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item {
                    ExpressionInputChip(
                        label = {
                            RootExpressionUi(expression = expression.action)
                        },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.rounded_motion_play_24),
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    ExpressionInputChip(
                        label = {
                            RootExpressionUi(expression = expression.data)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.DataObject,
                                contentDescription = "Data"
                            )
                        }
                    )
                }
                item {
                    ExpressionInputChip(
                        label = {
                            RootExpressionUi(expression = expression.componentName)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Android,
                                contentDescription = "Component name"
                            )
                        }
                    )
                }
            }
        }
//        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IntentPackageExpressionUiPreview() {
    PreviewTheme {
        IntentComponentNameExpressionUi(
            expression = IntentComponentNameExpression(
                action = ConstantExpression(Intent.ACTION_VIEW),
                data = UrlToAndroidUriExpression(
                    expression = KnownTokens.ResultUrl
                ),
                componentName = ComponentNameExpression(
                    pkg = ConstantExpression("com.dv.adm"),
                    cls = ConstantExpression("com.dv.adm.AEditor")
                )
            )
        )
    }
}
