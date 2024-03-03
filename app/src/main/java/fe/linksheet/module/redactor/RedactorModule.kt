package fe.linksheet.module.redactor

import fe.kotlin.extension.string.decodeHexOrThrow
import fe.linksheet.extension.koin.single
import fe.linksheet.module.log.impl.hasher.HashProcessor
import fe.linksheet.module.log.impl.hasher.LogDumpable
import fe.linksheet.module.log.impl.hasher.LogHasher
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.util.CryptoUtil
import org.koin.dsl.module

val redactorModule = module {
    single<Redactor, AppPreferenceRepository> { _, preferences ->
        @OptIn(SensitivePreference::class)
        val logKey = preferences.getOrWriteInit(AppPreferences.logKey).decodeHexOrThrow()

        DefaultRedactor(logKey)
    }
}

interface Redactor {
    fun <T> process(redact: Boolean, param: T, processor: HashProcessor<T>): String

    companion object {
        val HMAC = CryptoUtil.HmacSha("HmacSHA256", 64)
    }
}

class DefaultRedactor(private val key: ByteArray) : Redactor {
    private val logKeyHasher by lazy {
        LogHasher.LogKeyHasher(CryptoUtil.makeHmac(Redactor.HMAC.algorithm, key))
    }

    override fun <T> process(redact: Boolean, param: T, processor: HashProcessor<T>): String {
        val hasher = if (redact) logKeyHasher else LogHasher.NoOpHasher
        return LogDumpable.dumpObject(StringBuilder(), hasher, param, processor)
            .toString()
            .replace("%", "%%")
    }
}
