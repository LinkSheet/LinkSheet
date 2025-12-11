package app.linksheet.feature.app

import android.content.pm.ActivityInfo

val ActivityInfo.activityDescriptor
    get() = PackageIdHelper.getDescriptor(this)
