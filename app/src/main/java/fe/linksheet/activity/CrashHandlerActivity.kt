package fe.linksheet.activity

import android.content.ClipboardManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import com.junkfood.seal.ui.component.PreferenceSubtitle
import fe.linksheet.R
import fe.linksheet.composable.util.BottomRow
import fe.linksheet.extension.setText
import fe.linksheet.ui.AppHost
import fe.linksheet.ui.HkGroteskFontFamily

class CrashHandlerActivity : ComponentActivity() {
    companion object {
        const val extraCrashException = "EXTRA_CRASH_EXCEPTION_TEXT"
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exception = intent?.getStringExtra(extraCrashException)
        val clipboardManager = getSystemService<ClipboardManager>()!!

        setContent {
            AppHost {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                    canScroll = { true }
                )

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent),
                            title = {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    fontFamily = HkGroteskFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }, scrollBehavior = scrollBehavior
                        )
                    },
                    content = { padding ->
                        if (exception != null) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(padding)
                                        .weight(1f),
                                    contentPadding = PaddingValues(5.dp)
                                ) {
                                    stickyHeader(key = "header") {
                                        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                                            PreferenceSubtitle(
                                                text = stringResource(id = R.string.app_crashed),
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))
                                        }
                                    }


                                    item("exception") {
                                        SelectionContainer {
                                            Text(
                                                text = exception,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }


                               BottomRow {
                                    TextButton(
                                        onClick = {
                                            clipboardManager.setText(
                                                resources.getString(R.string.crash_log),
                                                exception
                                            )
                                        }
                                    ) {
                                        Text(text = stringResource(id = R.string.copy_exception))
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}