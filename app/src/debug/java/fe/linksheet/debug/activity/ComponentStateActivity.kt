package fe.linksheet.debug.activity

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.composable.util.DashedBorderBox
import fe.linksheet.extension.kotlinx.RefreshableStateFlow
import fe.linksheet.extension.kotlinx.collectRefreshableAsStateWithLifecycle
import kotlinx.coroutines.launch

class ComponentStateActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            AppTheme {
                Box(modifier = Modifier.systemBarsPadding()) {
                    Column(
                        modifier = Modifier.padding(all = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ComponentStateScreen(this@ComponentStateActivity)
                    }
                }
            }
        }
    }
}

private val settingFlags = mapOf(
    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT to "COMPONENT_ENABLED_STATE_DEFAULT",
    PackageManager.COMPONENT_ENABLED_STATE_ENABLED to "COMPONENT_ENABLED_STATE_ENABLED",
    PackageManager.COMPONENT_ENABLED_STATE_DISABLED to "COMPONENT_ENABLED_STATE_DISABLED",
    PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER to "COMPONENT_ENABLED_STATE_DISABLED_USER",
    PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED to "COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED"
)

@Composable
private fun ComponentStateScreen(context: Context) {
    val scope = rememberCoroutineScope()
    val searchComponent = remember { ComponentName(context, "fe.linksheet.intent.search") }
    val stateFlow = remember {
        RefreshableStateFlow(0) {
            context.packageManager.getComponentEnabledSetting(searchComponent)
        }
    }

    val enabledState by stateFlow.collectRefreshableAsStateWithLifecycle(0)
    val setting = remember(enabledState) { settingFlags[enabledState] }


    DashedBorderBox(
        text = AnnotatedString(text = "$searchComponent"),
        surface = MaterialTheme.colorScheme.surface,
        strokeWidth = 1.dp,
        color = Color.Gray,
        cornerRadius = 12.dp,
        padding = 8.dp
    ) {
        Text(text = "$setting")
    }

    Column {
        Button(onClick = { scope.launch { stateFlow.refresh() }}) {
            Text(text = "Refresh")
        }
        for ((flag, key) in settingFlags) {
            Button(onClick = {
                context.packageManager.setComponentEnabledSetting(
                    searchComponent,
                    flag,
                    PackageManager.DONT_KILL_APP
                )

                scope.launch { stateFlow.refresh() }
            }) {
                Text(text = "Set ${key.substring(18)}")
            }
        }
    }
}
