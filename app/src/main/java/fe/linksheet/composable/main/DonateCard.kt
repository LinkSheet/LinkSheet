package fe.linksheet.composable.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.aboutSettingsRoute
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.annotatedStringResource
import fe.linksheet.developmentTimeHours
import fe.linksheet.developmentTimeMonths
import fe.linksheet.donateSettingsRoute
import fe.linksheet.extension.androidx.navigate
import fe.linksheet.mainRoute
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.settingsRoute
import fe.linksheet.ui.Typography

@Composable
fun DonateCard(
    navController: NavHostController,
    useTime: Pair<Int?, Int?>
) {
    val (hours, minutes) = useTime
    val timeString = if (hours != null) {
        pluralStringResource(id = R.plurals.hours, hours, hours)
    } else pluralStringResource(id = R.plurals.minutes, minutes!!, minutes)

    val devTimeHoursString = pluralStringResource(
        id = R.plurals.hours,
        count = developmentTimeHours,
        developmentTimeHours
    )
    val devTimeMonthString = pluralStringResource(
        id = R.plurals.months,
        count = developmentTimeMonths,
        developmentTimeMonths
    )

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
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    SelectionContainer {
                        Text(
                            text = annotatedStringResource(
                                id = R.string.donate_card_subtitle,
                                timeString,
                                devTimeHoursString,
                                devTimeMonthString,
                                developmentTimeHours,
                                developmentTimeMonths
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = {
                            navController.navigate(donateSettingsRoute)
                        }) {
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