package app.linksheet.feature.shizuku.service

import android.content.ServiceConnection
import fe.std.result.StdResult
import fe.std.result.tryCatch
import rikka.shizuku.Shizuku

interface WrappedShizuku {
    fun pingBinder(): StdResult<Boolean>
    fun checkSelfPermission(): StdResult<Int>
    fun requestPermission(code: Int): StdResult<Unit>
    fun unbindUserService(args: Shizuku.UserServiceArgs, conn: ServiceConnection): StdResult<Unit>
    fun bindUserService(args: Shizuku.UserServiceArgs, conn: ServiceConnection): StdResult<Unit>
}

object RealWrappedShizuku : WrappedShizuku {
    override fun pingBinder(): StdResult<Boolean> {
        return tryCatch { Shizuku.pingBinder() }
    }

    override fun checkSelfPermission(): StdResult<Int> {
        return tryCatch { Shizuku.checkSelfPermission() }
    }

    override fun requestPermission(code: Int): StdResult<Unit> {
        return tryCatch { Shizuku.requestPermission(code) }
    }

    override fun unbindUserService(args: Shizuku.UserServiceArgs, conn: ServiceConnection): StdResult<Unit> {
        return tryCatch { Shizuku.unbindUserService(args, conn, true) }
    }

    override fun bindUserService(args: Shizuku.UserServiceArgs, conn: ServiceConnection): StdResult<Unit> {
        return tryCatch { Shizuku.bindUserService(args, conn) }
    }
}
