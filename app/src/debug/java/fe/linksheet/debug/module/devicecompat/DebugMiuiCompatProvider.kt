package fe.linksheet.debug.module.devicecompat

import android.app.AppOpsManager
import android.content.Context
import androidx.core.content.getSystemService
import app.linksheet.api.RefineWrapper
import app.linksheet.feature.devicecompat.miui.MiuiCompat
import app.linksheet.feature.devicecompat.miui.MiuiCompatProvider
import app.linksheet.feature.devicecompat.miui.RealMiuiCompat
import fe.std.lazy.resettableLazy

class DebugMiuiCompatProvider(private val refineWrapper: RefineWrapper) : MiuiCompatProvider {
    private var required = false

    override val isXiaomiDevice = true

    override fun readMiuiVersion() = 12

    override val isRequired = resettableLazy { required }

    override fun provideCompat(context: Context): MiuiCompat {
        val appOpsManager = context.getSystemService<AppOpsManager>()!!
        return RealMiuiCompat(refineWrapper.cast(appOpsManager))
    }

    fun toggleRequired(): Boolean {
        return setRequired(!required)
    }

    fun setRequired(required: Boolean): Boolean {
        this.required = required
        return isRequired.refresh()
    }
}
