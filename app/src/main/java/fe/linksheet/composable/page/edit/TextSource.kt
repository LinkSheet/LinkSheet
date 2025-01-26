package fe.linksheet.composable.page.edit

import androidx.annotation.StringRes
import fe.linksheet.R

sealed class TextSource(@StringRes val id: Int) {
    data object ClipboardCard : TextSource(R.string.home__clipboard_card_source)
}
