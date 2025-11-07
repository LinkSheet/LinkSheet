@file:OptIn(ExperimentalSerializationApi::class)

package app.linksheet.feature.engine.eval.expression

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.annotation.Keep
import app.linksheet.feature.engine.eval.EvalContext
import fe.composekit.intent.buildIntent
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Keep
@Serializable
@SerialName(OpCodes.COMPONENT_NAME)
internal class ComponentNameExpression(
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
@SerialName(OpCodes.COMPONENT_NAME_TO_INTENT)
internal class IntentComponentNameExpression(
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

@Keep
@Serializable
@SerialName(OpCodes.PACKAGE_TO_INTENT)
internal class IntentPackageExpression(
    @ProtoNumber(1)
    val action: Expression<String>,
    @ProtoNumber(2)
    val data: Expression<@Contextual Uri>,
    @ProtoNumber(3)
    val packageName: Expression<String>
) : Expression<Intent> {
    override fun eval(ctx: EvalContext): Intent {
        return buildIntent(action.eval(ctx), data.eval(ctx)) {
            `package` = packageName.eval(ctx)
        }
    }
}
