package fe.linksheet.activity.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.ui.HkGroteskFontFamily

@Preview(
    showBackground = true,
)
@Composable
fun LoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.loading_link),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold
        )
//        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp))
    }
}



