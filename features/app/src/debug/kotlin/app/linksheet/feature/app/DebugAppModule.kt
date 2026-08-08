package app.linksheet.feature.app

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
