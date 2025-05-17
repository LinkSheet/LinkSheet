package fe.linksheet.module.resolver.workaround

import android.content.ComponentName
import android.net.Uri
import androidx.core.net.toUri

object GithubWorkaround {
    private val latestReleasesRegex = Regex(
        "^((?:https?|github)://(?:.+\\.)?github\\.com/.+/.+/releases)/latest/?$"
    )

    private val `package` = ComponentName(
        "com.github.android", "com.github.android.activities.DeepLinkActivity"
    )

    fun tryFixUri(componentName: ComponentName, uri: Uri?): Uri? {
        if (componentName != `package`) return null

        return uri
            ?.let { latestReleasesRegex.matchEntire(it.toString()) }
            ?.groupValues
            ?.let { (_, releasesPath) -> releasesPath.toUri() }
    }
}
