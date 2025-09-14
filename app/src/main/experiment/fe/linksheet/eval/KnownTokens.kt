package fe.linksheet.eval

import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.eval.expression.InjectTokenExpression
import fe.std.uri.StdUrl

object KnownTokens {
    val ResultUrl = InjectTokenExpression<StdUrl>(name = "ru")
    val OriginalUrl = InjectTokenExpression<StdUrl>(name = "ou")
    val EngineRunContext = InjectTokenExpression<EngineRunContext>(name = "erc")
}
