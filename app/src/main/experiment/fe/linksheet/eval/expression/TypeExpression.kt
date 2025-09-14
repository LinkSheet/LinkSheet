@file:OptIn(ExperimentalSerializationApi::class)

package fe.linksheet.eval.expression

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.annotation.Keep
import fe.composekit.intent.buildIntent
import fe.linksheet.eval.EvalContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Keep
@Serializable
@SerialName(OpCodes.COMPONENT_NAME)
class ComponentNameExpression(
    @ProtoNumber(1)
    val pkg: Expression<String>,
    @ProtoNumber(2)
    val cls: Expression<String>
) : Expression<ComponentName> {
    override fun eval(ctx: EvalContext): ComponentName {
        return ComponentName(pkg.eval(ctx), cls.eval(ctx))
    }
}

@Keep
@Serializable
@SerialName(OpCodes.INTENT)
class IntentExpression(
    @ProtoNumber(1)
    val action: Expression<String>,
    @ProtoNumber(2)
    val data: Expression<@Contextual Uri>,
    @ProtoNumber(3)
    val componentName: Expression<@Contextual ComponentName>
) : Expression<Intent> {
    override fun eval(ctx: EvalContext): Intent {
        return buildIntent(action.eval(ctx), data.eval(ctx), componentName.eval(ctx))
    }
}

