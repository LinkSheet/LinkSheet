package app.linksheet.feature.app

import android.content.pm.queryIntentActivitiesCompat
import android.content.pm.resolveActivityCompat
import android.net.compatHost
import app.linksheet.api.SystemInfoService
import app.linksheet.feature.app.core.DefaultPackageIntentHandler
import app.linksheet.feature.app.core.PackageIntentHandler
import app.linksheet.lib.flavors.LinkSheetApp
import fe.droidkit.koin.getPackageManager
import org.koin.dsl.module

val DebugAppModule = module {
//    single<PackageIntentHandler> {
//        val applicationId = get<SystemInfoService>().getApplicationId()
//        val pm = getPackageManager()
//        DefaultPackageIntentHandler(
//            queryIntentActivities = { intent, flags ->
//                if (intent.data?.compatHost != null) return@DefaultPackageIntentHandler emptyList()
//                when (intent.data?.scheme) {
//                    "https" -> pm.queryIntentActivitiesCompat(intent, flags).take(4)
//                    else -> emptyList()
//                }
//            },
////            queryIntentActivities = pm::queryIntentActivitiesCompat,
//            resolveActivity = pm::resolveActivityCompat,
//            isLinkSheetCompat = { LinkSheetApp.Compat.isApp(it) != null },
//            isSelf = { applicationId == it },
//        )
//    }
}
