package fe.linksheet.debug.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.composable.ui.AppTheme

class LinkTestingActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            AppTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                ) {
                    LinkButton(
                        url = "https://google.github.io/accompanist/systemuicontroller/",
                        text = "accompanist docs"
                    )

                    LinkButton(
                        url = "https://www.amazon.ca/gp/f.html?C=DFWX1K3KY2H9&K=633AK191WV8U&M=urn:rtn:msg:20241120200338d406b78d6f69409682e941b4df80p0na&R=7PRHESQP8FDG&T=C&U=https%3A%2F%2Fprimevideo.com%2Fdetail%2Famzn1.dv.gti.82d895c4-99c9-4073-81f5-a42108b27a54%3Fref_%3Dpe_47689480_1031066840&H=6KR2J5NBJPX7QWQ7Y5ME7FFAPSWA&ref_=pe_47689480_1031066840",
                        text = "Amazon loop"
                    )

                    LinkButton(
                        url = "https://github.com/LinkSheet/LinkSheet",
                        text = "LinkSheet Github"
                    )
                }
            }
        }
    }

    @Composable
    private fun LinkButton(url: String, text: String) {
        Button(
            onClick = {
                this@LinkTestingActivity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url),
                        this@LinkTestingActivity,
                        BottomSheetActivity::class.java
                    )
                )
            }
        ) {
            Text(text = text)
        }
    }
}
