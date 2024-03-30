package fe.linksheet.debug.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.extension.compose.setContentWithKoin
import fe.linksheet.ui.AppHost
import org.koin.core.component.KoinComponent

class LinkTestingActivity : ComponentActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        initPadding()
        setContentWithKoin {
            AppHost {
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
