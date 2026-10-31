package fe.clearurlskt.provider

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import fe.gson.extension.io.parseJsonOrNull
import fe.gson.extension.json.array.elementsOrNull
import fe.gson.extension.json.`object`.asArray
import fe.gson.extension.json.`object`.asBooleanOrNull
import fe.gson.extension.json.`object`.asObjectOrNull
import fe.gson.extension.json.`object`.asString
import fe.gson.extension.json.`object`.elements
import java.io.InputStream

public object ProviderSerializer {
    private fun String.toIgnoreCaseRegex(exactly: Boolean = false): Regex {
        val str = if (exactly) "^$this$" else this
        return Regex(str, RegexOption.IGNORE_CASE)
    }

    private fun JsonObject.arrayByNameToIgnoreCaseRegexList(name: String, exactly: Boolean = false): List<Regex> {
        return asArray(name)
            .elementsOrNull<JsonPrimitive>()
            .mapNotNull { it?.asString?.toIgnoreCaseRegex(exactly) }
    }

    internal fun createProviderFromObject(key: String, index: Int, obj: JsonObject): ProviderV1 {
        return ProviderV1(
            // make sure the globalRules provider is the last one in the list and used as a fallback
            sortPosition = if (key == "globalRules") Int.MAX_VALUE else index,
            key,
            obj.asString("urlPattern").toIgnoreCaseRegex(),
            obj.asBooleanOrNull("completeProvider") == true,
            obj.arrayByNameToIgnoreCaseRegexList("rules", true),
            obj.arrayByNameToIgnoreCaseRegexList("rawRules"),
            obj.arrayByNameToIgnoreCaseRegexList("referralMarketing"),
            obj.arrayByNameToIgnoreCaseRegexList("exceptions"),
            obj.arrayByNameToIgnoreCaseRegexList("redirections"),
        )
    }

    private fun handleProviders(providers: JsonObject): List<ProviderV1> {
        return providers
            .elements<JsonObject>()
            .mapIndexed { idx, (key, obj) -> createProviderFromObject(key, idx, obj) }
            .sortedBy { it.sortPosition }
    }

    public fun handle(stream: InputStream): List<ProviderV1>? {
        return stream
            .parseJsonOrNull<JsonObject>()
            ?.asObjectOrNull("providers")
            ?.let { handleProviders(it) }
    }
}
