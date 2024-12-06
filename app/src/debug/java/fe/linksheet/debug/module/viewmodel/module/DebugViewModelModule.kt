package fe.linksheet.debug.module.viewmodel.module

import fe.linksheet.debug.module.viewmodel.DebugViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val DebugViewModelModule = module {
    viewModelOf(::DebugViewModel)
}
