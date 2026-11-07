package app.linksheet.feature.clearurls

import android.content.Context
import fe.clearurlskt.config.provider.ClearUrlBundleSerializer
import fe.clearurlskt.config.provider.ClearUrlV1Bundle
import fe.clearurlskt.config.provider.Provider
import fe.linksheet.R
import fe.std.result.StdResult
import fe.std.result.isFailure
import fe.std.result.tryCatch
import fe.std.result.unaryPlus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ClearUrlsProviderLoader(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getProviders(): StdResult<List<Provider>> {
        val bundleBytes = withContext(ioDispatcher) {
            context.resources
                .openRawResource(R.raw.clearurls)
                .buffered()
                .use { it.readBytes() }
        }
        val result = tryCatch {
            ClearUrlBundleSerializer.decodeBundle(bundleBytes)
        }
        if (result.isFailure()) return +result
        return when (val bundle = result.value) {
            is ClearUrlV1Bundle -> +bundle.providers
        }
    }
}
