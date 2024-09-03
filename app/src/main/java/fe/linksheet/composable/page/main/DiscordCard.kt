package fe.linksheet.composable.page.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.composable.ui.NewTypography

@Composable
fun DiscordCard(
    viewModel: MainViewModel,
    uriHandler: UriHandler
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            ColoredIcon(
                icon = Icons.AutoMirrored.Filled.Chat,
                descriptionId = R.string.discord,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(id = R.string.discord),
                    style = NewTypography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(id = R.string.discord_explainer),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { viewModel.showDiscordBanner(false) }) {
                Text(text = stringResource(id = R.string.dismiss))
            }

            Spacer(modifier = Modifier.width(5.dp))

            Button(onClick = { uriHandler.openUri(BuildConfig.LINK_DISCORD) }) {
                Text(text = stringResource(id = R.string.join))
            }
        }
    }
}
