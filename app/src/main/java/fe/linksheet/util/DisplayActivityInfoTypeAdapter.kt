package fe.linksheet.util

import android.content.pm.ResolveInfo
import android.net.Uri
import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import fe.gson.dsl.jsonArray
import fe.gson.dsl.jsonObject
import fe.gson.extension.json.element.array
import fe.gson.extension.json.element.`object`
import fe.gson.extension.json.`object`.asString
import fe.gson.typeadapter.ExtendedTypeAdapter
import fe.linksheet.resolver.DisplayActivityInfo
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.lang.reflect.Type


object UriTypeAdapter : ExtendedTypeAdapter<Uri>(Uri::class.java) {
    override fun read(read: JsonReader): Uri {
        return Uri.parse(read.nextString())
    }

    override fun write(out: JsonWriter, value: Uri) {
        out.value(value.toString())
    }
}

object HttpUrlTypeAdapter : ExtendedTypeAdapter<HttpUrl>(HttpUrl::class.java) {
    override fun read(read: JsonReader): HttpUrl {
        return read.nextString().toHttpUrlOrNull()!!
    }

    override fun write(out: JsonWriter, value: HttpUrl) {
        out.value(value.toString())
    }
}

class DisplayActivityInfoListSerializer(val resolveInfos: Map<String, Pair<ResolveInfo, String>>) :
    JsonSerializer<List<DisplayActivityInfo>>, JsonDeserializer<List<DisplayActivityInfo>> {
    override fun serialize(
        src: List<DisplayActivityInfo>,
        typeOfSrc: Type,
        context: JsonSerializationContext?,
    ): JsonElement {
        val packages = src.map { it.resolvedInfo.activityInfo.packageName }
        return jsonArray(packages)
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext?,
    ): List<DisplayActivityInfo> {
        val arr = json.array()
        return arr.map { obj ->
            val (resolveInfo, label) = resolveInfos[obj.`object`().asString("resolvedInfo")]!!
            DisplayActivityInfo(resolveInfo, label)
        }
    }

}

class DisplayActivityInfoSerializer(
    val resolveInfos: Map<String, Pair<ResolveInfo, String>>,
) : JsonSerializer<DisplayActivityInfo>, JsonDeserializer<DisplayActivityInfo> {
    override fun serialize(src: DisplayActivityInfo, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return jsonObject {
            "resolvedInfo" += src.resolvedInfo.activityInfo.packageName
            "label" += src.label
        }
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): DisplayActivityInfo {
        val obj = json.`object`()
        val (resolveInfo, label) = resolveInfos[obj.asString("resolvedInfo")]!!

        return DisplayActivityInfo(resolveInfo, label)
    }
}
