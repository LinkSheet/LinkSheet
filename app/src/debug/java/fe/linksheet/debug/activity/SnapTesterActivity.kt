package fe.linksheet.debug.activity

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.fix.ModalBottomSheet
import androidx.compose.material3.fix.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.composable.ui.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SnapTesterActivity : BaseComponentActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            AppTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                ) {
                    BottomSheetSnapTester()
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun BottomSheetSnapTester() {
        var show by remember { mutableStateOf(false) }
        var loading by remember { mutableStateOf(true) }

        val scope = rememberCoroutineScope()
        val state = rememberModalBottomSheetState()

        Button(onClick = {
            show = true
            scope.launch {
                delay(2000)
                loading = false
                state.expand()
            }
        }) {
            Text(text = "Show")
        }

        if (show) {
            ModalBottomSheet(
                sheetState = state,
                onDismissRequest = {
                    show = false
                    loading = true
                }
            ) {
                if (loading) {
                    Column(
                        modifier = Modifier.height(200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(modifier = Modifier.height(1300.dp)) {
                        Text(text = "test")
                    }
                }
            }
        }
    }
}
