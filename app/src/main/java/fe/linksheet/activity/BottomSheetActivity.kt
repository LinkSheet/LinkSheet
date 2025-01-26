package fe.linksheet.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import fe.linksheet.activity.bottomsheet.BottomSheetImpl
import fe.linksheet.activity.bottomsheet.ImprovedBottomSheet
import fe.linksheet.activity.bottomsheet.LoopDetector
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.util.intent.Intents
import kotlinx.coroutines.launch
import mozilla.components.support.utils.toSafeIntent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

// Must not be moved or renamed since LinkSheetCompat hardcodes the package/name
class BottomSheetActivity : BaseComponentActivity(), KoinComponent {
    private val logger by injectLogger<BottomSheetActivity>()
    private val viewModel by viewModel<BottomSheetViewModel>()

    private lateinit var impl: BottomSheetImpl
    private var loopDetector = LoopDetector(this)

    val editorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        logger.info("editorLauncher: $result")

        if(result.resultCode == RESULT_OK && result.data != null) {
            val uri = result.data?.getStringExtra(TextEditorActivity.EXTRA_TEXT)
                ?.let { Uri.parse(it) }
            onNewIntent(Intents.createSelfIntent(uri))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.info("onCreate")

        lifecycleScope.launch { loopDetector.setInitialIntent(intent) }
        impl = ImprovedBottomSheet(loopDetector, editorLauncher, this, viewModel, intent.toSafeIntent(), referrer)
        impl.onCreate(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        logger.info("onStop")

        impl.onStop()
    }

    override fun onResume() {
        super.onResume()
        logger.info("onResume")
    }

    override fun onPause() {
        super.onPause()
        logger.info("onPause")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        logger.info("onNewIntent: $intent")

        lifecycleScope.launch { loopDetector.onNewIntent(intent) }
        impl.onNewIntent(intent)
    }
}
