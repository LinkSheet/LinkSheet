package fe.linksheet.composable.settings.advanced

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.composable.util.*

@Composable
fun ExperimentDialog(experiment: String) {
    DialogColumn {
        HeadlineText(headline = "Enable experiment $experiment")
        DialogSpacer()

        SubtitleText(subtitle = "Do you want to enable the experiment $experiment?")
        DialogSpacer()
        BottomRow {
            TextButton(
                onClick = {

                }
            ) {
                Text(text = stringResource(id = R.string.no))
            }

            TextButton(
                onClick = {

                }
            ) {
                Text(text = stringResource(id = R.string.yes))
            }
        }
    }
}
