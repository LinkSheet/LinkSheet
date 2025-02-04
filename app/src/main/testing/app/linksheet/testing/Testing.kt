package app.linksheet.testing

import android.os.Build

object Testing {
    val IsTestRunner by lazy { Build.FINGERPRINT.equals("robolectric", ignoreCase = true) }
}
