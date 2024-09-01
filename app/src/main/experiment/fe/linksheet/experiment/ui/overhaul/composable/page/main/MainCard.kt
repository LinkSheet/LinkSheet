package fe.linksheet.experiment.ui.overhaul.composable.page.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.android.compose.extension.optionalClickable
import fe.linksheet.R
import fe.linksheet.ui.PreviewTheme


@Composable
fun MainCard(
    colors: CardColors = CardDefaults.cardColors(),
    onClick: (() -> Unit)?,
    innerPadding: PaddingValues = PaddingValues(all = 16.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Card(
        colors = colors,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .optionalClickable(onClick)
                .padding(innerPadding),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Preview
@Composable
fun MainCardPreview() {
    PreviewTheme {
        MainCard(onClick = { /*TODO*/ }) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.inverseOnSurface), contentAlignment = Alignment.Center
            ) {
                Icon(
//               modifier = Modifier.background(color = MaterialTheme.colorScheme.inverseOnSurface),
                    imageVector = Icons.Filled.NewReleases,
                    contentDescription = stringResource(id = R.string.nightly_experiments_card),
                )
            }

            Column {
                Text(
                    text = stringResource(id = R.string.nightly_experiments_card),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Text(
                    text = stringResource(id = R.string.nightly_experiments_card_explainer),
                )
            }
        }
    }
}
