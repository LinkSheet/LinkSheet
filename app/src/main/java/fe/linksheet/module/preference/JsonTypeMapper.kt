package fe.linksheet.module.preference

import com.google.gson.Gson
import fe.android.preference.helper.Mapper
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.TypeMapper
import fe.android.preference.helper.Unmapper
import fe.gson.util.Json

@Suppress("FunctionName")
inline fun <reified T> JsonTypeMapper(gson: Gson): TypeMapper<T, String> {
    return object : TypeMapper<T, String> {
        override val unmap: Unmapper<String, T> = { Json.fromJsonOrNull(it, gson) }
        override val map: Mapper<T, String> = { Json.toJson(it, gson) }
    }
}

inline fun <reified T : Any> PreferenceDefinition.jsonMapped(
    key: String,
    default: T,
    gson: Gson = Gson()
): Preference.Mapped<T, String> {
    val mapper = JsonTypeMapper<T>(gson)
    return mapped(key, default, mapper)
}
