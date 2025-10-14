package fe.linksheet.module.shizuku

import android.os.IBinder
import android.os.IInterface
import fe.std.result.IResult
import fe.std.result.tryCatch
import rikka.shizuku.ShizukuBinderWrapper


fun IBinder.wrapShizuku(): ShizukuBinderWrapper {
    return ShizukuBinderWrapper(this)
}

fun IInterface.asShizukuBinder(): ShizukuBinderWrapper {
    return asBinder().wrapShizuku()
}

