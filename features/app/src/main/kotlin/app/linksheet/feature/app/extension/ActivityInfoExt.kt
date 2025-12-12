package app.linksheet.feature.app.extension

import android.content.pm.ActivityInfo
import app.linksheet.feature.app.core.PackageIdHelper

val ActivityInfo.activityDescriptor
    get() = PackageIdHelper.getDescriptor(this)
