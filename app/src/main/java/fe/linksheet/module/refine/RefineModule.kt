package fe.linksheet.module.refine

import android.os.UserHandle
import android.os.UserHandleHidden
import app.linksheet.api.RefineWrapper
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

    override fun myUserId(): Int {
        return UserHandleHidden.myUserId()
    }
}
