package fe.linksheet.experiment.ui.overhaul.composable.main

import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import fe.linksheet.module.viewmodel.MainViewModel


@Composable
fun BrowserCard(browserStatus: MainViewModel.BrowserStatus) {
    MainCard(colors = CardDefaults.cardColors(containerColor = browserStatus.containerColor()), onClick = null) {
        MainCardContent(
            icon = browserStatus.icon,
            iconDescription = browserStatus.iconDescription,
            title = browserStatus.headline,
            content = browserStatus.subtitle
        )
    }

//    Card(
//        colors = CardDefaults.cardColors(containerColor = browserStatus.containerColor()),
//        shape = RoundedCornerShape(16.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 10.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically
//        ) {
//            Spacer(modifier = Modifier.width(10.dp))
//            ColoredIcon(
//                icon = browserStatus.icon,
//                descriptionId = browserStatus.iconDescription,
//                color = browserStatus.color()
//            )
//
//            Column(modifier = Modifier.padding(10.dp)) {
//                Text(
//                    text = stringResource(id = browserStatus.headline),
//                    style = Typography.titleLarge,
//                    color = browserStatus.color()
//                )
//                Text(
//                    text = stringResource(id = browserStatus.subtitle),
//                    color = browserStatus.color()
//                )
//            }
//        }
//    }
}
