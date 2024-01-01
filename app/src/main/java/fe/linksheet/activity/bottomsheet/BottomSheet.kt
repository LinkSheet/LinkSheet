package fe.linksheet.activity.bottomsheet

import android.content.Intent
import android.content.res.Resources
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.extension.android.initPadding
import fe.linksheet.extension.android.showToast
import fe.linksheet.extension.android.startPackageInfoActivity
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.DisplayActivityInfo

abstract class BottomSheet(
    private val bottomSheetActivity: BottomSheetActivity,
    protected val bottomSheetViewModel: BottomSheetViewModel
) {
    abstract fun show()

    protected val lifecycleScope = bottomSheetActivity.lifecycleScope
    protected val intent: Intent = bottomSheetActivity.intent
    protected val referrer = bottomSheetActivity.referrer
    protected val resources: Resources = bottomSheetActivity.resources
    protected val packageName: String = bottomSheetActivity.packageName

    protected fun initPadding() {
        bottomSheetActivity.initPadding()
    }

    protected fun setContent(content: @Composable () -> Unit) {
        bottomSheetActivity.setContent(content = content)
    }

    protected fun showToast(@StringRes textId: Int, uiThread: Boolean = false) {
        bottomSheetActivity.showToast(textId = textId, uiThread = uiThread)
    }

    protected fun showToast(text: String, uiThread: Boolean = false) {
        bottomSheetActivity.showToast(text = text, uiThread = uiThread)
    }

    protected fun startActivity(intent: Intent) {
        bottomSheetActivity.startActivity(intent)
    }

    protected fun finish() {
        bottomSheetActivity.finish()
    }

    protected fun startPackageInfoActivity(info: DisplayActivityInfo) {
        bottomSheetActivity.startPackageInfoActivity(info)
    }

    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return bottomSheetActivity.getString(resId, *formatArgs)
    }
}
