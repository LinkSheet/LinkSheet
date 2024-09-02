package fe.linksheet.composable.page.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.rememberAnnotatedStringResource
import fe.linksheet.donateSettingsRoute
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.NewTypography

@Composable
fun DonateCard(
    navController: NavHostController,
    viewModel: MainViewModel,
    useTime: Pair<Int?, Int?>,
) {


    val subtitle = rememberAnnotatedStringResource(
        R.string.donate_card_subtitle

    )

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
                .heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                ColoredIcon(
                    icon = Icons.Default.Euro,
                    descriptionId = R.string.donate,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(id = R.string.donate_card_headline),
                        style = NewTypography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    SelectionContainer {
                        Text(
                            text = subtitle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { viewModel.donateCardDismissed(true) }) {
                            Text(text = stringResource(id = R.string.dismiss))
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        Button(onClick = { navController.navigate(donateSettingsRoute) }) {
                            Text(
                                text = stringResource(id = R.string.donate_learn_more),
                            )
                        }
                    }
                }
            }
        }
    }
}
