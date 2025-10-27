package app.linksheet.feature.shizuku

import androidx.compose.foundation.lazy.LazyListScope
import app.linksheet.compose.DebugMenuButton

fun LazyListScope.shizukuDebugItem() {
    item(key = "shizuku-test") {
        DebugMenuButton(
            text = "Shizuku",
            onClick = {

            }
        )
    }
}
