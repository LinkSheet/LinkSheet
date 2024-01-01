package fe.linksheet.composable.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.discordInvite
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.Typography

@Composable
fun NewBottomSheetCard(viewModel: MainViewModel, ) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(12.dp),
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
                icon = Icons.Filled.NewReleases,
                descriptionId = R.string.discord,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(id = R.string.new_bottomsheet),
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                LinkableTextView(
                    id = R.string.new_bottomsheet_explainer,
                    style = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 16.sp
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                viewModel.updateState(viewModel.showNewBottomSheetBanner, false)
            }) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        }
    }
}
