package fe.linksheet.module.devicecompat

import fe.linksheet.module.devicecompat.miui.MiuiCompat
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.samsung.SamsungIntentCompat
import fe.linksheet.module.devicecompat.samsung.SamsungIntentCompatProvider
import org.koin.dsl.module

val CompatModule = module {
    single<MiuiCompat> { get<MiuiCompatProvider>().provideCompat(get()) }
    single<SamsungIntentCompat> { get<SamsungIntentCompatProvider>().provideCompat(get()) }
}
