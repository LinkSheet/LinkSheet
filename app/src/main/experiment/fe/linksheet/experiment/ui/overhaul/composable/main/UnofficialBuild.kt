package fe.linksheet.experiment.ui.overhaul.composable.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import fe.linksheet.R

@Composable
fun UnofficialBuild() {
    MainCard(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error), onClick = null) {
        MainCardContent(
            icon = Icons.Default.Warning,
            iconDescription = R.string.warning,
            title = R.string.running_unofficial_build,
            content = R.string.built_by_error
        )
    }

//    MainCard(
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error),
//        onClick = null,
//    ) {
//        Icon(
//            imageVector = Icons.Default.Warning,
//            contentDescription = stringResource(id = R.string.warning),
//        )
//
//        Column {
//            Text(
//                text = stringResource(id = R.string.running_unofficial_build),
//                style = Typography.titleLarge,
//            )
//
//            Text(
//                text = stringResource(id = R.string.built_by_error),
//                fontSize = 16.sp
//            )
//        }
//    }

//    Card(
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
//        shape = RoundedCornerShape(16.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 10.dp)
//    ) {CX
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically
//        ) {
//            Spacer(modifier = Modifier.width(10.dp))
//            ColoredIcon(
//                icon = Icons.Default.Warning,
//                descriptionId = R.string.warning,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//
//            Column(modifier = Modifier.padding(10.dp)) {
//                Text(
//                    text = stringResource(id = R.string.running_unofficial_build),
//                    style = Typography.titleLarge,
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//                Text(
//                    text = stringResource(id = R.string.built_by_error),
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//            }
//        }
//    }
}
