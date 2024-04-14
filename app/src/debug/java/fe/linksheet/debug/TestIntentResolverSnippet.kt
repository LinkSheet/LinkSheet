package fe.linksheet.debug

import android.content.pm.ResolveInfo
import android.net.Uri
import fe.linksheet.experiment.improved.resolver.PackageInstallHelper
import fe.linksheet.extension.kotlin.mapProducingSideEffect
import fe.linksheet.module.database.entity.PreferredApp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class TestIntentResolverSnippet {
//    public inline fun <T, R> T.runIf(condition: Boolean, block: T.() -> R): R {
//        if (condition) block()
//        return block()
//    }
//
//    private fun getLauncherOrDelete(app: PreferredApp?, delete: (PreferredApp) -> Unit): ResolveInfo? {
//        val launcher = PackageInstallHelper.getLauncherOrNull(context, app!!.pkg)
//        if (launcher == null) delete(app)
//
//        return launcher
//    }
//
//    private fun getLaunchersOrDelete(
//        lastUsedApps: Map<String, Long>,
//        delete: (List<String>) -> Unit,
//    ): Map<ResolveInfo, Long> {
//        val (launchers, toDelete) = PackageInstallHelper.hasLauncher(context, lastUsedApps.keys)
//        if (toDelete.isNotEmpty()) delete(toDelete)
//
//        return launchers.associateWith { lastUsedApps[it.activityInfo.packageName]!! }
//    }
//
//    private suspend fun queryStoredAppStatus(
//        dispatcher: CoroutineDispatcher = Dispatchers.IO,
//        uri: Uri,
//    ) = withContext(dispatcher) {
//        val preferred = preferredAppRepository.getByHost2(uri)?.filter { it != null }?.mapProducingSideEffect(
//            transform = { app, delete ->
//                PackageInstallHelper.getLauncherOrNull(context, app!!.pkg)
//            },
//            handleSideEffect = preferredAppRepository::delete
//        )?.firstOrNull()
//
//        val lastUsedApps = appSelectionHistoryRepository.getLastUsedForHostGroupedByPackage2(uri)
//            ?.mapProducingSideEffect(
//                transform = ::getLaunchersOrDelete,
//                handleSideEffect = appSelectionHistoryRepository::delete
//            )?.firstOrNull()
//
//        preferred to lastUsedApps
//    }
}
