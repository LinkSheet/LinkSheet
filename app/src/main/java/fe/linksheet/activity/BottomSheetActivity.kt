package fe.linksheet.activity

import android.os.Bundle
import fe.linksheet.activity.bottomsheet.BottomSheetActivityImpl
import fe.linksheet.activity.bottomsheet.BottomSheetImpl
import fe.linksheet.experiment.improved.resolver.activity.bottomsheet.ImprovedBottomSheet
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

// Must not be moved or renamed since LinkSheetCompat hardcodes the package/name
class BottomSheetActivity : BaseComponentActivity() {
    private val viewModel by viewModel<BottomSheetViewModel>()
    private lateinit var impl: BottomSheetImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        impl = if (!viewModel.improvedIntentResolver()) {
            BottomSheetActivityImpl(this, viewModel)
        } else {
            ImprovedBottomSheet(this, viewModel, intent, referrer)
        }

        impl.onCreate(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        impl.onStop()
    }
}
