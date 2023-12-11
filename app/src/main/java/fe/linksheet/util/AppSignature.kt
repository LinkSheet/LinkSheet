package fe.linksheet.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import fe.linksheet.R
import java.util.Locale

object AppSignature {
    private lateinit var buildType: BuildType

    enum class BuildType(@StringRes val stringRes: Int) {
        Manual(R.string.manual_build),
        GithubPipeline(R.string.github_pipeline_build),
        Unofficial(R.string.built_by_error),
        Debug(R.string.debug_build)
    }

    private val officialSigningKeys = mapOf(
        "C2A8B18C328DFB39C896491757ED11C145D3ACCA43212FA3DE362433C416AAA9" to BuildType.Manual,
        "3FCF7675BC90E239892C7262499DCC9F8CE6A52B7E58D02B56AA60CA669D6701" to BuildType.GithubPipeline,
        "6113B71A2850288E65A35D85182FE751F30D5F89106396EB544DD770A08CEE2F" to BuildType.Debug
    )

    fun checkSignature(context: Context): BuildType {
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