package app.linksheet.feature.shizuku

import android.os.IBinder
import android.os.IInterface
import rikka.shizuku.ShizukuBinderWrapper


fun IBinder.wrapShizuku(): ShizukuBinderWrapper {
    return ShizukuBinderWrapper(this)
}

fun IInterface.asShizukuBinder(): ShizukuBinderWrapper {
    return asBinder().wrapShizuku()
}

