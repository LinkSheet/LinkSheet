package fe.linksheet.module.resolver

import android.app.ActivityManager
import android.content.Context
import androidx.core.content.getSystemService
import fe.linksheet.util.runIfOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object IconLoader : KoinComponent {
    private val context by inject<Context>()
    private val activityManager = context.getSystemService<ActivityManager>()!!

    fun getIcon(packageName: String, resId: Int) = runIfOrNull(resId != 0) {
        kotlin.runCatching {
            context.packageManager
                .getResourcesForApplication(packageName)
                .getDrawableForDensity(resId, activityManager.launcherLargeIconDensity, null)
        }.getOrNull()
    }
}