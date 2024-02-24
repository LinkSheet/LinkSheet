package fe.linksheet.module.lifecycle

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import org.koin.dsl.module

val processLifecycleCoroutineModule = module {
    single<LifecycleCoroutineScope> { ProcessLifecycleOwner.get().lifecycleScope }
}
