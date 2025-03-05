package fe.linksheet.module.devicecompat

import fe.linksheet.module.devicecompat.miui.MiuiCompat
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.oneui.OneUiCompat
import fe.linksheet.module.devicecompat.oneui.OneUiCompatProvider
import org.koin.dsl.module

val CompatModule = module {
    single<MiuiCompat> { get<MiuiCompatProvider>().provideCompat(get()) }
    single<OneUiCompat> { get<OneUiCompatProvider>().provideCompat(get()) }
}
