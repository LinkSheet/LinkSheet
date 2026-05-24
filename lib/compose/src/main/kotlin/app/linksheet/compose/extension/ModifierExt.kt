package app.linksheet.compose.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

fun Modifier.testTagIfNotNull(tag: String?): Modifier {
    return if(tag == null) this else testTag(tag)
}
