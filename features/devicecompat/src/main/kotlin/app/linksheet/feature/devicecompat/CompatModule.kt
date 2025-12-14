package app.linksheet.feature.devicecompat

import app.linksheet.feature.devicecompat.miui.MiuiCompat
import app.linksheet.feature.devicecompat.miui.MiuiCompatProvider
import app.linksheet.feature.devicecompat.oneui.OneUiCompat
import app.linksheet.feature.devicecompat.oneui.OneUiCompatProvider
import org.koin.dsl.module

val CompatModule = module {
    single<MiuiCompat> { get<MiuiCompatProvider>().provideCompat(get()) }
    single<OneUiCompat> { get<OneUiCompatProvider>().provideCompat(get()) }
}
