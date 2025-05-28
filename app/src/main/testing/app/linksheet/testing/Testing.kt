package app.linksheet.testing

import android.os.Build
import fe.linksheet.BuildConfig

object Testing {
    val IsTestRunner by lazy { Build.FINGERPRINT.equals("robolectric", ignoreCase = true) || BuildConfig.IS_CI }
}
