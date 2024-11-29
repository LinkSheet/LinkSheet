package fe.linksheet.activity

import android.content.Intent
import android.os.Bundle
import fe.linksheet.activity.bottomsheet.BottomSheetActivityImpl
import fe.linksheet.activity.bottomsheet.BottomSheetImpl
import fe.linksheet.experiment.improved.resolver.LoopDetectorExperiment
import fe.linksheet.experiment.improved.resolver.activity.bottomsheet.ImprovedBottomSheet
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import mozilla.components.support.utils.toSafeIntent
import org.koin.androidx.viewmodel.ext.android.viewModel

// Must not be moved or renamed since LinkSheetCompat hardcodes the package/name
class BottomSheetActivity : BaseComponentActivity() {
    private val viewModel by viewModel<BottomSheetViewModel>()
    private lateinit var impl: BottomSheetImpl
    private var loopDetectorExperiment: LoopDetectorExperiment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!viewModel.improvedIntentResolver()) {
            impl = BottomSheetActivityImpl(this, viewModel)
            impl.onCreate(savedInstanceState)
            return
        }

        loopDetectorExperiment = if (viewModel.loopDetector()) LoopDetectorExperiment(this) else null
        impl = ImprovedBottomSheet(loopDetectorExperiment, this, viewModel, intent.toSafeIntent(), referrer)
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
