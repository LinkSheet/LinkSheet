@file:OptIn(ExperimentalSerializationApi::class)

package app.linksheet.feature.engine.eval.expression

import android.content.Intent
import androidx.annotation.Keep
import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.ForwardOtherProfileResult
import app.linksheet.feature.engine.core.IntentEngineResult
import app.linksheet.feature.engine.core.UrlEngineResult
import app.linksheet.feature.engine.eval.EvalContext
import fe.std.uri.StdUrl
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber


sealed interface EngineResultExpression : Expression<EngineResult>

@Keep
@Serializable
@SerialName(OpCodes.URL_ENGINE_RESULT)
class UrlEngineResultExpression(
    @ProtoNumber(1)
    val expression: Expression<@Contextual StdUrl>,
) : EngineResultExpression {
    override fun eval(ctx: EvalContext): EngineResult {
        return UrlEngineResult(expression.eval(ctx))
    }
}

@Keep
@Serializable
@SerialName(OpCodes.INTENT_ENGINE_RESULT)
class IntentEngineResultExpression(
    @ProtoNumber(1)
    val expression: Expression<@Contextual Intent>,
) : EngineResultExpression {
    override fun eval(ctx: EvalContext): EngineResult {
        return IntentEngineResult(expression.eval(ctx))
    }
}

@Keep
@Serializable
@SerialName(OpCodes.FORWARD_OTHER_PROFILE_RESULT)
class ForwardOtherProfileResultExpression(
    @ProtoNumber(1)
    val expression: Expression<@Contextual StdUrl>,
) : EngineResultExpression {
    override fun eval(ctx: EvalContext): EngineResult {
        return ForwardOtherProfileResult(expression.eval(ctx))
    }
}
