package fe.linksheet.module.shizuku

import android.os.IBinder
import android.os.IInterface
import fe.std.result.IResult
import fe.std.result.tryCatch
import rikka.shizuku.ShizukuBinderWrapper


fun IBinder.wrapShizuku(): IResult<ShizukuBinderWrapper> {
    return tryCatch { ShizukuBinderWrapper(this) }
}

fun IInterface.asShizukuBinder(): IResult<ShizukuBinderWrapper> {
    return asBinder().wrapShizuku()
}

