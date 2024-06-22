package fe.linksheet.debug.activity

import androidx.compose.material3.fix.ModalBottomSheet
import androidx.compose.material3.fix.rememberModalBottomSheetState
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fe.linksheet.ui.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class DebugActivity : ComponentActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                BottomSheetSnapTester()
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
                    Column(modifier = Modifier.height(200.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
