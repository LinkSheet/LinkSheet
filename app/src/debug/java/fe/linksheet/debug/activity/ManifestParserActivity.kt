package fe.linksheet.debug.activity

import android.content.Context
import android.content.pm.getApplicationInfoCompat
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
import app.linksheet.feature.app.core.ManifestParser
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.composable.ui.AppTheme
import org.koin.core.component.KoinComponent

class ManifestParserActivity: BaseComponentActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            AppTheme {
                Box(modifier = Modifier.systemBarsPadding()) {
                    Column(
                        modifier = Modifier.padding(all = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ManifestParserScreen(this@ManifestParserActivity)
                    }
                }
            }
        }
    }
}

@Composable
private fun ManifestParserScreen(context: Context) {
    val list = remember { mutableStateListOf<String>() }
    Button(onClick = {
        val info = context.packageManager.getApplicationInfoCompat("app.linksheet.interconnect.testapp")
        val parser = ManifestParser()
        list.addAll(parser.parse(info.sourceDir))
    }) {
        Text(text = "Parse")
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = list) {
            Text(text = it)
        }
    }
}
