package fe.linksheet.debug.activity

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.debug.activity.locale.LocaleScreen

class LocaleDebugActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            AppTheme {
                Box(modifier = Modifier.systemBarsPadding()) {
                    LocaleScreen()
                }
            }
        }
    }
}
