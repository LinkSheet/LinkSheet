package fe.linksheet.activity.bottomsheet

import android.os.Bundle

abstract class BottomSheetImpl {
    abstract fun onCreate(savedInstanceState: Bundle?)

    abstract fun onStop()
}
