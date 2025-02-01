package fe.linksheet.experiment.engine.resolver

import android.net.Uri

interface LinkResolver {
   suspend fun resolve(data: ResolveInput): ResolveOutput?
}

data class ResolveInput(val url: String) {
   val uri by lazy { Uri.parse(url) }
}

data class ResolveOutput(val url: String)



