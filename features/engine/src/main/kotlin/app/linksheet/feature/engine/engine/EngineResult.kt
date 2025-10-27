package app.linksheet.feature.engine.engine

import android.content.Intent
import app.linksheet.feature.engine.engine.context.SealedRunContext
import fe.std.uri.StdUrl

interface EngineResult {
}

class IntentEngineResult(val intent: Intent) : EngineResult
class UrlEngineResult(val url: StdUrl) : EngineResult
class ForwardOtherProfileResult(val url: StdUrl) : EngineResult

typealias ContextualEngineResult = Pair<SealedRunContext, EngineResult>
