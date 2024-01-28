package fe.linksheet.composable.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.Typography


@Composable
fun BrowserCard(browserStatus: MainViewModel.BrowserStatus) {
    Card(
        colors = CardDefaults.cardColors(containerColor = browserStatus.containerColor()),
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
                icon = browserStatus.icon,
                descriptionId = browserStatus.iconDescription,
                color = browserStatus.color()
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(id = browserStatus.headline),
                    style = Typography.titleLarge,
                    color = browserStatus.color()
                )
                Text(
                    text = stringResource(id = browserStatus.subtitle),
                    color = browserStatus.color()
                )
            }
        }
    }
}
