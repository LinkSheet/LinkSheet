@file:OptIn(ExperimentalSerializationApi::class)

package fe.linksheet.eval.expression

import android.content.Intent
import androidx.annotation.Keep
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.ForwardOtherProfileResult
import fe.linksheet.experiment.engine.IntentEngineResult
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.eval.EvalContext
import fe.std.uri.StdUrl
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber


@Keep
@Serializable
@SerialName(OpCodes.URL_ENGINE_RESULT)
class UrlEngineResultExpression(
    @ProtoNumber(1)
    private val expression: Expression<@Contextual StdUrl>,
) : Expression<EngineResult> {
    override fun eval(ctx: EvalContext): EngineResult {
        return UrlEngineResult(expression.eval(ctx))
    }
}

@Keep
@Serializable
@SerialName(OpCodes.INTENT_ENGINE_RESULT)
class IntentEngineResultExpression(
    @ProtoNumber(1)
    private val expression: Expression<@Contextual Intent>,
) : Expression<EngineResult> {
    override fun eval(ctx: EvalContext): EngineResult {
        return IntentEngineResult(expression.eval(ctx) )
    }
}

@Keep
@Serializable
@SerialName(OpCodes.FORWARD_OTHER_PROFILE_RESULT)
class ForwardOtherProfileResultExpression(
    @ProtoNumber(1)
    private val expression: Expression<@Contextual StdUrl>,
) : Expression<EngineResult> {
    override fun eval(ctx: EvalContext): EngineResult {
        return ForwardOtherProfileResult(expression.eval(ctx) )
    }
}
