package fe.linksheet.experiment.engine

import android.content.Intent
import fe.linksheet.experiment.engine.context.SealedRunContext
import fe.std.uri.StdUrl

interface EngineResult

class IntentEngineResult(val intent: Intent) : EngineResult
class UrlEngineResult(val url: StdUrl) : EngineResult
class ForwardOtherProfileResult(val url: StdUrl) : EngineResult

typealias ContextualEngineResult = Pair<SealedRunContext, EngineResult>
