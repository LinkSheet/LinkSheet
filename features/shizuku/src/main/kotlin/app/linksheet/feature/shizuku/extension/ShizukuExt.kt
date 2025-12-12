package app.linksheet.feature.shizuku.extension

import android.os.IBinder
import android.os.IInterface
import rikka.shizuku.ShizukuBinderWrapper


fun IBinder.wrapShizuku(): ShizukuBinderWrapper {
    return ShizukuBinderWrapper(this)
}

fun IInterface.asShizukuBinder(): ShizukuBinderWrapper {
    return asBinder().wrapShizuku()
}

