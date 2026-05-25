package app.linksheet.test.e2e

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import fe.linksheet.testlib.instrument.InstrumentationTest
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.opentest4j.TestAbortedException


@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultBrowserParameter

fun InstrumentationTest.createDefaultBrowserExtension(): DefaultBrowserExtension {
    return DefaultBrowserExtension(targetContext)
}

class DefaultBrowserExtension internal constructor(
    private val targetContext: Context
) : ParameterResolver {

    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Boolean {
        return with(parameterContext.parameter) {
            isAnnotationPresent(DefaultBrowserParameter::class.java) && type == DefaultBrowser::class.java
        }
    }

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any {
        return getDefaultBrowser(targetContext) ?: throw TestAbortedException("No default browser available")
    }
}

data class DefaultBrowser(
    val applicationLabel: String,
    val intentHandlerLabel: String,
    val packageName: String
)

private fun getDefaultBrowser(targetContext: Context): DefaultBrowser? {
    val defaultBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
    val activityInfo = targetContext.packageManager
        .resolveActivity(defaultBrowserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        ?.activityInfo
        ?: return null

    return DefaultBrowser(
        applicationLabel = targetContext.packageManager.getApplicationLabel(activityInfo.applicationInfo).toString(),
        intentHandlerLabel = activityInfo.loadLabel(targetContext.packageManager).toString(),
        packageName = activityInfo.packageName
    )
}
