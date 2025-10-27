@file:OptIn(ExperimentalSerializationApi::class)

package app.linksheet.feature.engine.eval.expression

import androidx.annotation.Keep
import app.linksheet.feature.engine.core.context.AppRoleId
import app.linksheet.feature.engine.core.context.EngineExtra
import app.linksheet.feature.engine.core.context.EngineFlag
import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.context.IgnoreLibRedirectExtra
import app.linksheet.feature.engine.core.context.KnownBrowserExtra
import app.linksheet.feature.engine.core.context.SkipFollowRedirectsExtra
import app.linksheet.feature.engine.core.context.SourceAppExtra
import app.linksheet.feature.engine.core.context.findExtraOrNull
import app.linksheet.feature.engine.core.context.hasExtra
import app.linksheet.feature.engine.eval.EvalContext
import fe.linksheet.util.AndroidAppPackage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.reflect.KClass

@Keep
@Serializable
@SerialName(OpCodes.HAS_EXTRA)
class HasExtraExpression(
    @ProtoNumber(1)
    val op: Expression<EngineRunContext>,
    @ProtoNumber(2)
    val extra: Expression<Extra>
) : Expression<Boolean> {
    override fun eval(ctx: EvalContext): Boolean {
        return op.eval(ctx).hasExtra(extra.eval(ctx).toEngineExtra())
    }
}

@Keep
@Serializable
@SerialName(OpCodes.GET_SOURCE_APP_EXTRA)
class GetSourceAppExtraExpression(
    @ProtoNumber(1)
    val expression: Expression<EngineRunContext>
) : Expression<String?> {
    override fun eval(ctx: EvalContext): String? {
        return expression.eval(ctx).findExtraOrNull<SourceAppExtra>()?.appPackage
    }
}

@Keep
@Serializable
@SerialName(OpCodes.ADD_FLAG)
class AddFlagExpression(
    @ProtoNumber(1)
    val expression: Expression<EngineRunContext>,
    @ProtoNumber(2)
    val flag: Expression<EngineFlag>
) : Expression<Boolean> {
    override fun eval(ctx: EvalContext): Boolean {
        return expression.eval(ctx).flags.add(flag.eval(ctx))
    }
}

@Keep
@Serializable
@SerialName(OpCodes.PUT_APP_ROLE)
class PutAppRoleExpression(
    @ProtoNumber(1)
    val expression: Expression<EngineRunContext>,
    @ProtoNumber(2)
    val id: Expression<AppRoleId>,
    @ProtoNumber(3)
    val packageName: Expression<String>
) : Expression<Boolean> {
    override fun eval(ctx: EvalContext): Boolean {
        val appPackage = AndroidAppPackage(packageName.eval(ctx))
        return expression.eval(ctx).put(id.eval(ctx), appPackage)
    }
}

enum class Extra {
    SourceApp,
    KnownBrowser,
    IgnoreLibRedirect,
    SkipFollowRedirects
}

fun Extra.toEngineExtra(): KClass<out EngineExtra> {
    return when (this) {
        Extra.SourceApp -> SourceAppExtra::class
        Extra.KnownBrowser -> KnownBrowserExtra::class
        Extra.IgnoreLibRedirect -> IgnoreLibRedirectExtra::class
        Extra.SkipFollowRedirects -> SkipFollowRedirectsExtra::class
    }
}
