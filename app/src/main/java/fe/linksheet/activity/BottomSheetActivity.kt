package fe.linksheet.activity

import android.content.Intent
import android.os.Bundle
import fe.linksheet.activity.bottomsheet.BottomSheetImpl
import fe.linksheet.activity.bottomsheet.LoopDetector
import fe.linksheet.activity.bottomsheet.ImprovedBottomSheet
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import mozilla.components.support.utils.toSafeIntent
import org.koin.androidx.viewmodel.ext.android.viewModel

// Must not be moved or renamed since LinkSheetCompat hardcodes the package/name
class BottomSheetActivity : BaseComponentActivity() {
    private val viewModel by viewModel<BottomSheetViewModel>()

    private lateinit var impl: BottomSheetImpl
    private var loopDetector = LoopDetector(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        impl = ImprovedBottomSheet(loopDetector, this, viewModel, intent.toSafeIntent(), referrer)
        impl.onCreate(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        impl.onStop()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        impl.onNewIntent(intent)
    }
}
