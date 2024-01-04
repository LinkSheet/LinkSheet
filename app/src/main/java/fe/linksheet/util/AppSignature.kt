package fe.linksheet.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.officialSigningKeys
import java.util.Locale

object AppSignature {
    private lateinit var buildType: BuildType

    enum class BuildType(@StringRes val stringRes: Int) {
        Manual(R.string.manual_build),
        GithubPipeline(R.string.github_pipeline_build),
        Unofficial(R.string.built_by_error),
        Debug(R.string.debug_build)
    }

    fun checkSignature(context: Context): BuildType {
        if (BuildConfig.DEBUG) return BuildType.Debug
        if (this::buildType.isInitialized) return buildType

        val signature = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_SIGNATURES
        ).signatures[0]

        val certFingerprint = CryptoUtil.sha256Hex(
            signature.toByteArray()
        ).uppercase(
            Locale.getDefault()
        )
        return officialSigningKeys.getOrDefault(certFingerprint, BuildType.Unofficial).also {
            buildType = it
        }
    }
}
