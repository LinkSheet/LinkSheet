package fe.clearurlskt.loader

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import fe.clearurlskt.Resource
import fe.clearurlskt.provider.Provider
import fe.clearurlskt.provider.ProviderSerializer
import fe.signify.Ed25519
import fe.signify.Ed25519PublicKey
import fe.signify.Type
import java.io.InputStream
import java.net.URL

public interface ClearURLConfigLoader {
    public fun load(): Result<List<Provider>?>
}

public object BundledClearURLConfigLoader : ClearURLConfigLoader {
    private const val FILE = "clearurls.json"
    private val bundledClass = Resource::class.java
    private val bundledClassPackage by lazy {
        bundledClass.`package`.name.replace(".", "/")
    }

    private val url by lazy {
        bundledClass.getResource(FILE)
            ?: getSystemResource(bundledClassPackage)
            ?: getSystemResource("fe/clearurlskt")
            ?: getSystemResource()
    }

    public fun getSystemResource(path: String? = null): URL? {
        val filePath = path?.let { "$it/$FILE" } ?: FILE
        return ClassLoader.getSystemResource(filePath)
    }

    override fun load(): Result<List<Provider>?> {
        return runCatching {
            url?.openStream()?.let { ProviderSerializer.handle(it) }
        }
    }
}

public class StreamClearURLConfigLoader(private val stream: InputStream) : ClearURLConfigLoader {
    override fun load(): Result<List<Provider>?> {
        return runCatching {
            ProviderSerializer.handle(stream)
        }
    }
}

public object RemoteLoader {
    public val gson: Gson = GsonBuilder().create()
    public val publicKey: Ed25519PublicKey = Ed25519.fromBase64String(Type.PublicKey,"RWQazSQ29JJBtHn/Vze0iWHWGlkMUlKFQLOt2EdbTo4ToTx40uV8r8N/").getOrNull()!!

    public inline fun <reified T> parseIfValid(fileStream: InputStream, signatureStream: InputStream): T? {
        val fileContent = fileStream.bufferedReader().readText()

        val signatureContent = signatureStream.bufferedReader().readLines()
        // TODO: Catch
        val signature = Ed25519.fromBase64String(Type.Signature,signatureContent.singleOrNull() ?: signatureContent[1]).getOrNull()!!

        return runCatching {
            publicKey.verify(signature, fileContent.toByteArray())
            gson.fromJson(fileContent, T::class.java)
        }.getOrNull()
    }
}



