@file:OptIn(ExperimentalSerializationApi::class)

package fe.linksheet.eval.expression

import android.net.Uri
import androidx.annotation.Keep
import fe.linksheet.extension.std.toAndroidUri
import fe.linksheet.eval.EvalContext
import fe.std.uri.StdUrl
import fe.std.uri.extension.new
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Keep
@Serializable
@SerialName(OpCodes.URL_GET_COMPONENT)
class UrlGetComponentExpression(
    @ProtoNumber(1)
    private val expression: Expression<@Contextual StdUrl>,
    @ProtoNumber(2)
    private val component: Expression<Component>
) : Expression<String> {
    override fun eval(ctx: EvalContext): String {
        val url = expression.eval(ctx)
        return when (component.eval(ctx)) {
            Component.Host -> url.host
            Component.Path -> url.pathStringWithoutFirstSlash
            Component.Query -> url.formattedQuery
        }
    }
}

@Keep
@Serializable
@SerialName(OpCodes.URL_SET_COMPONENT)
class UrlSetComponentExpression(
    @ProtoNumber(1)
    private val expression: Expression<@Contextual StdUrl>,
    @ProtoNumber(2)
    private val component: Expression<Component>,
    @ProtoNumber(3)
    private val value: Expression<String>,
) : Expression<StdUrl> {
    override fun eval(ctx: EvalContext): StdUrl {
        val url = expression.eval(ctx)
        val component = component.eval(ctx)
        val value = value.eval(ctx)

        return when (component) {
            Component.Host -> url.new { setHost(value) }
            Component.Path -> url.new { setPath(value) }
            Component.Query -> url.new { setCustomQuery(value) }
        }
    }
}


enum class Component {
    Host,
    Path,
    Query
}

@Keep
@Serializable
@SerialName(OpCodes.URL_QUERY_PARAM)
class UrlQueryParamExpression(
    @ProtoNumber(1)
    private val op: Expression<@Contextual StdUrl>,
    @ProtoNumber(2)
    private val key: Expression<String>
) : Expression<String?> {
    override fun eval(ctx: EvalContext): String? {
        return op.eval(ctx).firstQueryParamOrNull(key.eval(ctx))
    }
}

@Keep
@Serializable
@SerialName(OpCodes.URL_STRING)
class UrlStringExpression(
    @ProtoNumber(1)
    private val expression: Expression<@Contextual StdUrl>,
) : Expression<String> {
    override fun eval(ctx: EvalContext): String {
        return expression.eval(ctx).toString()
    }
}

@Keep
@Serializable
@SerialName(OpCodes.URL_TO_ANDROID_URI)
class UrlToAndroidUriExpression(
    @ProtoNumber(1)
    val expression: Expression<@Contextual StdUrl>,
) : Expression<Uri> {
    override fun eval(ctx: EvalContext): Uri {
        return expression.eval(ctx).toAndroidUri()
    }
}
