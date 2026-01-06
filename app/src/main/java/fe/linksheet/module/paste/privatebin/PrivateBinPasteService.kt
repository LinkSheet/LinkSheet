package fe.linksheet.module.paste.privatebin


import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fe.gson.dsl.jsonObject
import fe.gson.extension.json.`object`.asInt
import fe.gson.extension.json.`object`.asString
import fe.gson.util.jsonArrayItems
import fe.httpkt.Request
import fe.httpkt.json.JsonBody
import fe.httpkt.json.readToJsonElement
import fe.linksheet.module.paste.Paste
import fe.linksheet.module.paste.PasteService
import fe.linksheet.util.Base58
import fe.linksheet.util.CryptoUtil
import java.net.HttpURLConnection
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class PrivateBinPaste(
    url: String,
    val deleteToken: String
) : Paste(url)

class PrivateBinPasteService(val config: PrivateBinConfig) : PasteService<PrivateBinPaste> {
    companion object {
        val request = Request {
            headers { "X-Requested-With" += "JSONHttpRequest" }
        }
    }

    override fun createPaste(message: String): Result<PrivateBinPaste> {
        return createPaste(message, "", config)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun createMetadata(
        iv: ByteArray,
        kdfSalt: ByteArray,
        kdfIterations: Int,
        kdfSize: Int,
        tagLength: Int,
        format: String,
        openDiscussion: Int,
        burnAfterRead: Int,
        algorithm: String,
        cipher: String,
        compression: String
    ): JsonArray {
        val encryption = arrayOf<Any>(
            Base64.encode(iv), Base64.encode(kdfSalt), kdfIterations, kdfSize,
            tagLength, algorithm, cipher, compression
        )

        return jsonArrayItems(encryption, format, openDiscussion, burnAfterRead)
    }

    private fun encrypt(
        pasteKey: String,
        tagLength: Int,
        kdfIterations: Int,
        kdfSize: Int,
        format: String,
        openDiscussion: Int,
        burnAfterRead: Int,
        compressedMessage: String,
        password: String
    ): Pair<JsonArray, ByteArray> {
        val kdfSalt = CryptoUtil.getRandomBytes(8)
        val iv = CryptoUtil.getRandomBytes(16)

        val metadata = createMetadata(
            iv = iv,
            kdfSalt = kdfSalt,
            kdfIterations = kdfIterations,
            kdfSize = kdfSize,
            tagLength = tagLength,
            format = format,
            openDiscussion = openDiscussion,
            burnAfterRead = burnAfterRead,
            algorithm = "aes",
            cipher = "gcm",
            compression = "none"
        )

        val key = CryptoUtil.deriveKey(
            "PBKDF2WithHmacSHA256",
            (pasteKey + password).toCharArray(),
            kdfSalt,
            kdfIterations,
            kdfSize
        )

        val aesKey = SecretKeySpec(key.encoded, "AES")

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(tagLength, iv)

        cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec)
        cipher.updateAAD(metadata.toString().encodeToByteArray())

        val encrypted = cipher.doFinal(compressedMessage.encodeToByteArray())
        return metadata to encrypted
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun createPayload(
        message: String,
        password: String,
        config: PrivateBinConfig,
    ): Pair<String, JsonObject> {
        val pasteDataJson = jsonObject {
            "paste" += message
        }

        val pasteKey = Base64.encode(CryptoUtil.generateKey("AES", config.aesSize).encoded)

        val (metadata, encryptedMessage) = encrypt(
            pasteKey = pasteKey,
            tagLength = config.tagLength,
            kdfIterations = config.kdfIterations,
            kdfSize = config.aesSize,
            format = config.format,
            openDiscussion = config.openDiscussion,
            burnAfterRead = config.burnAfterRead,
            compressedMessage = pasteDataJson.toString(),
            password = password
        )

        val payload = jsonObject {
            "ct" += Base64.encode(encryptedMessage)
            "v" += 2
            "meta" += jsonObject {
                "expire" += config.expire
            }

            "adata" += metadata
        }

        return pasteKey to payload
    }

    private fun createPaste(
        message: String,
        password: String,
        config: PrivateBinConfig
    ): Result<PrivateBinPaste> {
        val (pasteKey, payload) = createPayload(message, password, config)

        val con = request.post(config.baseUrl, body = JsonBody(payload))
        return parseResponse(con).map { response ->
            val (id, deleteToken) = response

            val key = Base58.encode(pasteKey.encodeToByteArray())
            val pasteUrl = "${config.baseUrl}/?$id#$key"

            PrivateBinPaste(pasteUrl, deleteToken)
        }
    }


    @Throws(Exception::class)
    private fun parseResponse(con: HttpURLConnection): Result<Pair<String, String>> {
        return runCatching {
            val response = con.readToJsonElement<JsonObject>()
            val status = response.asInt("status")
            if (status != 0) throw Exception("Failed to upload, status: $status")

            val id = response.asString("id")
            val deleteToken = response.asString("deletetoken")

            id to deleteToken
        }
    }
}
