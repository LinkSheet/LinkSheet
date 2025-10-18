package fe.embed.resolve.config

import fe.embed.resolve.loader.RemoteLoader
import java.io.InputStream

public interface Config

public object ConfigSerializer {

    public inline fun <reified T : Config> parseConfig(inputStream: InputStream): T {
        return inputStream.bufferedReader().use { RemoteLoader.gson.fromJson(it, T::class.java) }
    }
}
