package app.linksheet.feature.engine.eval

import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.eval.expression.InjectTokenExpression
import fe.std.uri.StdUrl

object KnownTokens {
    val ResultUrl = InjectTokenExpression<StdUrl>(name = "ru")
    val OriginalUrl = InjectTokenExpression<StdUrl>(name = "ou")
    val EngineRunContext = InjectTokenExpression<EngineRunContext>(name = "erc")
}
