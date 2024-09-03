package fe.linksheet.debug.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.composable.ui.AppTheme

class LinkTestingActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            AppTheme {
                Button(onClick = {
                    this@LinkTestingActivity.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://google.github.io/accompanist/systemuicontroller/"),
                            this@LinkTestingActivity,
                            BottomSheetActivity::class.java
                        )
                    )
                }) {
                    Text(text = "accompanist docs")
                }
            }
        }
    }
}
