package fe.embed.resolve

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import fe.gson.extension.nextStringOrNull
import java.io.IOException

public object RegexTypeAdapter : TypeAdapter<Regex>() {
    @Throws(IOException::class)
    override fun write(writer: JsonWriter, pattern: Regex?) {
        writer.value(pattern?.pattern)
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Regex? {
        return reader.nextStringOrNull()?.let { Regex(it) }
    }
}

