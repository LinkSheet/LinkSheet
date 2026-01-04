package fe.linksheet.module.refine

import android.app.AppOpsManager
import android.app.AppOpsManagerHidden
import android.os.UserHandle
import android.os.UserHandleHidden
import app.linksheet.api.RefineWrapper
import app.linksheet.api.WrappedAppOpsManagerHidden
import app.linksheet.api.WrappedUserHandleHidden
import dev.rikka.tools.refine.Refine
import org.koin.dsl.module

val RefineModule = module {
    single<RefineWrapper> { RealRefineWrapper() }
}

class RealRefineWrapper : RefineWrapper {
    override fun <T> unsafeCast(value: Any): T {
        return Refine.unsafeCast<T>(value)
    }

    override fun cast(userHandle: UserHandle): WrappedUserHandleHidden {
        val casted = Refine.unsafeCast<UserHandleHidden>(userHandle)
        return WrappedUserHandleHidden(casted.identifier)
    }

    override fun cast(appOpsManager: AppOpsManager): WrappedAppOpsManagerHidden {
        val casted = Refine.unsafeCast<AppOpsManagerHidden>(appOpsManager)
        return WrappedAppOpsManagerHidden(casted::checkOp)
    }

    override fun myUserId(): Int {
        return UserHandleHidden.myUserId()
    }

    override fun getSystemProperty(key: String): String? {
        return android.os.SystemProperties.get(key)
    }
}
