package fe.linksheet.debug.activity

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.getActivityInfoCompatOrNull
import android.content.pm.getPackageInfoCompatOrNull
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.linksheet.feature.app.core.DefaultMetaDataHandler
import fe.linksheet.BuildConfig
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.composable.ui.AppTheme
import org.koin.core.component.KoinComponent

class MetaDataHandlerActivity : BaseComponentActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            AppTheme {
                Box(modifier = Modifier.systemBarsPadding()) {
                    Column(
                        modifier = Modifier.padding(all = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetaDataHandlerScreen(this@MetaDataHandlerActivity)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaDataHandlerScreen(context: Context) {
    val handler = remember {
        DefaultMetaDataHandler(
            getActivityInfoCompatOrNull = context.packageManager::getActivityInfoCompatOrNull,
            getPackageInfoCompatOrNull = context.packageManager::getPackageInfoCompatOrNull,
            selfPackage = BuildConfig.APPLICATION_ID,
            setComponentEnabledSetting = { _, _, _ -> }
        )
    }

    val list = remember { mutableStateListOf<ActivityInfo?>() }
    Button(onClick = {
        val activities = handler.getForwardProfileActivities()
        list.addAll(activities)
    }) {
        Text(text = "Get forward profile activities")
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = list) {
            Text(text = "$it")
        }
    }
}
