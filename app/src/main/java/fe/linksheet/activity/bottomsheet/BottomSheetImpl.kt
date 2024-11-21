package fe.linksheet.activity.bottomsheet

import android.content.Intent
import android.os.Bundle

abstract class BottomSheetImpl {
    abstract fun onCreate(savedInstanceState: Bundle?)

    abstract fun onStop()
    abstract fun onNewIntent(intent: Intent)
}
