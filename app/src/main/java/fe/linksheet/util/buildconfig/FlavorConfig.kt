package fe.linksheet.util.buildconfig

import androidx.annotation.Keep
import com.google.gson.JsonObject
import fe.gson.extension.json.`object`.asBooleanOrNull
import fe.gson.extension.json.`object`.asStringOrNull
import fe.gson.util.Json
import fe.std.result.getOrNull
import fe.std.result.tryCatch

@Keep
data class FlavorConfig(
    val isPro: Boolean,
    val supabaseHost: String,
    val supabaseApiKey: String,
) {
    companion object {
        val Default = FlavorConfig(false, "", "")

        fun parseFlavorConfig(config: String?): FlavorConfig {
            val flavorConfig = config ?: return Default

            val result = tryCatch {
                val obj = Json.parseJsonOrNull<JsonObject>(flavorConfig)
                val isPro = obj?.asBooleanOrNull("isPro") == true
                val supabaseHost = obj?.asStringOrNull("supabaseHost") ?: ""
                val supabaseApiKey = obj?.asStringOrNull("supabaseApiKey") ?: ""

                FlavorConfig(isPro, supabaseHost, supabaseApiKey)
            }

            return result.getOrNull() ?: Default
        }
    }
}
