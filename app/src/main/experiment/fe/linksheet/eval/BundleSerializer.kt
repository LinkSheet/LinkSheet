@file:OptIn(ExperimentalSerializationApi::class)

package fe.linksheet.eval

import androidx.annotation.Keep
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.eval.expression.Expression
import kotlinx.serialization.*
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

open class BundleSerializer internal constructor(
    private val protobuf: ProtoBuf
) {
    companion object Default : BundleSerializer(protobuf = ProtoBuf.Default)

    private fun <R, E : Expression<R>> makeBundle(version: Int, expression: E): ExpressionBundle {
        @Suppress("UNCHECKED_CAST")
        return ExpressionBundle(version, expression as Expression<EngineResult>)
    }

    fun <R, E : Expression<R>> encodeToHexString(version: Int, expression: E): String {
        return encodeToHexString(makeBundle(version, expression))
    }

    fun encodeToHexString(expression: ExpressionBundle): String {
        return protobuf.encodeToHexString(expression)
    }

    fun <R, E : Expression<R>> encodeToByteArray(version: Int, expression: E): ByteArray {
        return encodeToByteArray(makeBundle(version, expression))
    }

    fun encodeToByteArray(expression: ExpressionBundle): ByteArray {
        return protobuf.encodeToByteArray(expression)
    }

    fun decodeFromHexString(hexString: String): ExpressionBundle {
        return protobuf.decodeFromHexString(hexString)
    }

    fun decodeFromByteArray(bytes: ByteArray): ExpressionBundle {
        return protobuf.decodeFromByteArray(bytes)
    }
}

@Keep
@Serializable
@SerialName("-")
class ExpressionBundle(
    @ProtoNumber(1)
    val version: Int,
    @ProtoNumber(2)
    val expression: Expression<EngineResult>,
)
