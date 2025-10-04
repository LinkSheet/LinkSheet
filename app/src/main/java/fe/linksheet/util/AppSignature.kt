package fe.linksheet.util

import android.content.Context
import android.content.pm.getSignature
import androidx.annotation.StringRes
import app.linksheet.lib.flavors.LinkSheet
import app.linksheet.lib.flavors.Signature
import fe.linksheet.BuildConfig
import fe.linksheet.R

object AppSignature {
    private lateinit var buildType: SignatureBuildType

    enum class SignatureBuildType(val signature: Signature?, @StringRes val stringRes: Int) {
        Manual(Signature._1fexd, R.string.manual_build),
        CI(Signature._1fexdCI, R.string.github_pipeline_build),
        Unofficial(null, R.string.built_by_error),
        Debug(null, R.string.debug_build)
    }

    fun checkSignature(context: Context): SignatureBuildType {
        if (BuildConfig.DEBUG) return SignatureBuildType.Debug
        if (this::buildType.isInitialized) return buildType

        val signature = context.packageManager.getSignature(context.packageName)
        val knownSignature = LinkSheet.isValidSignature(CryptoUtil.sha256Hex(signature!!.toByteArray()))
        buildType = SignatureBuildType.entries.first { it.signature == knownSignature }

        return buildType
    }
}
