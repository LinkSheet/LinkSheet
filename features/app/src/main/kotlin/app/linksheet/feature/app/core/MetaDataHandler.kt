package app.linksheet.feature.app.core

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.getActivityInfoCompatOrNull
import android.content.pm.getPackageInfoCompatOrNull
import android.content.pm.setComponentEnabledSettingCompat
import android.os.Bundle
import fe.composekit.extension.componentName
import fe.linksheet.util.ComponentEnabledFlags
import fe.linksheet.util.ComponentEnabledStateFlags
import fe.linksheet.util.ComponentInfoFlags
import fe.linksheet.util.PackageInfoFlags
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MetaDataHandler {
    fun getMetaData(activity: Activity): Bundle?
    fun hasForwardProfile(activity: Activity): Boolean
    fun getForwardProfileActivities(): List<ActivityInfo>
    suspend fun setComponentEnabled(
        componentName: ComponentName,
        state: ComponentEnabledStateFlags,
        flags: ComponentEnabledFlags = ComponentEnabledFlags.DONT_KILL_APP
    )

    companion object {
        const val METADATA_KEY_FORWARD_PROFILE = "app.linksheet.profile.FORWARD"
    }
}

fun DefaultMetaDataHandler(context: Context, applicationId: String): MetaDataHandler {
    val pm = context.packageManager
    return DefaultMetaDataHandler(
        getActivityInfoCompatOrNull = pm::getActivityInfoCompatOrNull,
        getPackageInfoCompatOrNull = pm::getPackageInfoCompatOrNull,
        setComponentEnabledSetting = pm::setComponentEnabledSettingCompat,
        selfPackage = applicationId
    )
}

class DefaultMetaDataHandler(
    private val getActivityInfoCompatOrNull: (ComponentName, ComponentInfoFlags) -> ActivityInfo?,
    private val getPackageInfoCompatOrNull: (String, PackageInfoFlags) -> PackageInfo?,
    private val setComponentEnabledSetting: (ComponentName, ComponentEnabledStateFlags, ComponentEnabledFlags) -> Unit,
    private val selfPackage: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : MetaDataHandler {

    override fun getMetaData(activity: Activity): Bundle? {
        val activityInfo = getActivityInfoCompatOrNull(activity.componentName, ComponentInfoFlags.GET_META_DATA)
        return activityInfo?.metaData
    }

    override fun hasForwardProfile(activity: Activity): Boolean {
        val bundle = getMetaData(activity)
        return bundle?.getBoolean(MetaDataHandler.METADATA_KEY_FORWARD_PROFILE) == true
    }

    override fun getForwardProfileActivities(): List<ActivityInfo> {
        val applicationInfo = getPackageInfoCompatOrNull(
            selfPackage, PackageInfoFlags.select(
                PackageInfoFlags.GET_ACTIVITIES,
                PackageInfoFlags.GET_DISABLED_COMPONENTS,
                PackageInfoFlags.GET_META_DATA
            )
        )

        return applicationInfo
            ?.activities
            ?.filterNotNull()
            ?.filter { it.metaData?.getBoolean(MetaDataHandler.METADATA_KEY_FORWARD_PROFILE, false) == true }
            ?: emptyList()
    }

    override suspend fun setComponentEnabled(
        componentName: ComponentName,
        state: ComponentEnabledStateFlags,
        flags: ComponentEnabledFlags
    ) = withContext(dispatcher){
        setComponentEnabledSetting(componentName, state, flags)
    }
}

suspend fun MetaDataHandler.setForwardProfileActivities(state: Boolean) {
    val activities = getForwardProfileActivities()
    for (info in activities) {
        setComponentEnabled(
            info.componentName,
            if (state) ComponentEnabledStateFlags.COMPONENT_ENABLED_STATE_ENABLED else ComponentEnabledStateFlags.COMPONENT_ENABLED_STATE_DISABLED,
        )
    }
}
