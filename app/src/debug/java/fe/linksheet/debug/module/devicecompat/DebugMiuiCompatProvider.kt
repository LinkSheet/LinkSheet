package fe.linksheet.debug.module.devicecompat

import android.app.AppOpsManager
import android.content.Context
import androidx.core.content.getSystemService
import fe.linksheet.module.devicecompat.miui.MiuiCompat
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.miui.RealMiuiCompat
import fe.std.lazy.resettableLazy

object DebugMiuiCompatProvider : MiuiCompatProvider {
    private var required = false

    override val isXiaomiDevice = true

    override fun readMiuiVersion() = 12

    override val isRequired = resettableLazy { required }

    override fun provideCompat(context: Context): MiuiCompat {
        val appOpsManager = context.getSystemService<AppOpsManager>()!!
        return RealMiuiCompat(appOpsManager)
    }

    fun toggleRequired(): Boolean {
        return setRequired(!required)
    }

    fun setRequired(required: Boolean): Boolean {
        this.required = required
        return isRequired.refresh()
    }
}
