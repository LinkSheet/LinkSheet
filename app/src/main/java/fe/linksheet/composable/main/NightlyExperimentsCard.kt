package fe.linksheet.composable.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.experimentSettingsRoute
import fe.linksheet.extension.compose.clickable
import fe.linksheet.featureFlagSettingsRoute
import fe.linksheet.ui.Typography

@Composable
fun NightlyExperimentsCard(navController: NavHostController) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .clickable { navController.navigate(experimentSettingsRoute) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Filled.NewReleases,
                contentDescription = stringResource(id = R.string.nightly_experiments_card),
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(id = R.string.nightly_experiments_card),
                    style = Typography.titleLarge,
                )

                Text(
                    text = stringResource(id = R.string.nightly_experiments_card_explainer),
                    fontSize = 16.sp
                )
            }
        }
    }
}
