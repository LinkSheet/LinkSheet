package fe.linksheet.module.paste

import fe.linksheet.module.paste.privatebin.PrivateBinConfig
import fe.linksheet.module.paste.privatebin.PrivateBinPasteService
import org.koin.dsl.module

interface PasteService<T : Paste> {
    fun createPaste(message: String): Result<T>
}

abstract class Paste(val url: String)

val PasteServiceModule = module {
    single<PasteService<*>> { PrivateBinPasteService(PrivateBinConfig.Default) }
}
