package fe.clearurlkt

import fe.clearurlskt.loader.BundledClearURLConfigLoader
import fe.clearurlskt.provider.ProviderV1
import fe.clearurlskt.provider.ProviderV2
import fe.clearurlskt.provider.RegexRule
import fe.clearurlskt.provider.RuleType
import fe.std.enum.EnumFlagSet
import fe.std.enum.enumFlagSet
import fe.std.kotlinx.serialization.extension.EnumFlagSetSerializer
import fe.std.result.getOrNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test

@OptIn(ExperimentalSerializationApi::class)
class ProviderConvertTest {
    @Test
    fun test() {
        val result = BundledClearURLConfigLoader.load().getOrNull()!!
        val providersV2 = result.map { it.toV2() }
        val serializersModule = SerializersModule {
            contextual(EnumFlagSet::class) { _ ->
                EnumFlagSetSerializer(RuleType::class)
            }
        }
        val json = Json {
            this.serializersModule = serializersModule
        }
        val protoBuf = ProtoBuf {
            this.serializersModule = serializersModule
        }
//        println(json.encodeToString(providersV2))
        println(protoBuf.encodeToHexString(providersV2))
    }
}

private fun ProviderV1.toV2(): ProviderV2 {
    return ProviderV2(
        sortPosition = this.sortPosition,
        key = this.key,
        url = this.url,
        completeProvider = this.completeProvider,
        rules = this.rules.map {
            RegexRule(
                ruleType = enumFlagSet(RuleType.QueryParameter, RuleType.Fragment),
                regex = it
            )
        },
        rawRules = this.rawRules,
        referralMarketing = this.referralMarketing,
        exceptions = this.exceptions,
        redirections = this.redirections
    )
}

