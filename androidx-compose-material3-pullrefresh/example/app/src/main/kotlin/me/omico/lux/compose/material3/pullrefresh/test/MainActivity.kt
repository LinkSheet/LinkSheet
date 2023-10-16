package me.omico.lux.compose.material3.pullrefresh.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.omico.lux.compose.material3.pullrefresh.test.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // From https://developer.android.com/reference/kotlin/androidx/compose/material/pullrefresh/package-summary#(androidx.compose.ui.Modifier).pullRefresh(androidx.compose.material.pullrefresh.PullRefreshState,kotlin.Boolean)
                val refreshScope = rememberCoroutineScope()
                var refreshing by remember { mutableStateOf(false) }
                var itemCount by remember { mutableStateOf(15) }

                fun refresh() = refreshScope.launch {
                    refreshing = true
                    delay(1500)
                    itemCount += 5
                    refreshing = false
                }

                val state = rememberPullRefreshState(refreshing, ::refresh)

                Box(modifier = Modifier.pullRefresh(state = state)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                    ) {
                        if (!refreshing) {
                            items(itemCount) {
                                Card {
                                    Box(modifier = Modifier.padding(all = 8.dp)) {
                                        Text(text = "Item ${itemCount - it}")
                                    }
                                }
                            }
                        }
                    }
                    PullRefreshIndicator(
                        modifier = Modifier.align(alignment = Alignment.TopCenter),
                        refreshing = refreshing,
                        state = state,
                    )
                }
            }
        }
    }
}
