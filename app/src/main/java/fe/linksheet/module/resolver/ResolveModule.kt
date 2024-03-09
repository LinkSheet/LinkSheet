package fe.linksheet.module.resolver

import androidx.annotation.StringRes
import fe.linksheet.R
import fe.linksheet.util.StringResHolder

sealed class ResolveModule(val key: String, @StringRes override val id: Int, @StringRes val stringResId: Int) : StringResHolder {
    data object Amp2Html : ResolveModule("amp2html",R.string.amp2html_short, R.string.amp2html)
    data object Redirect : ResolveModule("redirect", R.string.follow_redirects_short, R.string.follow_redirects)
}
