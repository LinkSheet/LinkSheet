package com.tasomaniac.openwith.preferred

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.IconLoader
import fe.linksheet.extension.toDisplayActivityInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URI

object PreferredResolver : KoinComponent {
    private val database by inject<LinkSheetDatabase>()

    fun resolve(context: Context, uri: Uri): PreferredDisplayActivityInfo? {
        val host = uri.host
        if (host.isNullOrEmpty()) return null

        Log.d("PreferredResolver", "Host: $host")

        return database.preferredAppDao().preferredAppByHost(host)?.let { app ->
            Log.d("PreferredResolver", "App: $app")
            app.resolve(context)?.let {
                Log.d("PreferredResolver", "DisplayActivityInfo: $it")
                PreferredDisplayActivityInfo(app, it)
            }
        }
    }

    fun PreferredApp.resolve(context: Context): DisplayActivityInfo? {
        val intent = Intent().setComponent(componentName)
        val resolveInfo = context.packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )

        return resolveInfo?.toDisplayActivityInfo(context)
    }
}
