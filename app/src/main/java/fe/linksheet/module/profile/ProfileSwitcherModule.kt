package fe.linksheet.module.profile

import fe.droidkit.koin.getResources
import fe.droidkit.koin.getSystemServiceOrThrow
import fe.linksheet.R.string
import org.koin.dsl.module

val ProfileSwitcherModule = module {
    single {
        CrossProfileAppsCompat(get())
    }
    single {
        AndroidProfileSwitcherModule(
            appLabel = getResources().getString(string.app_name),
            crossProfileAppsCompat = get(),
            userManager = getSystemServiceOrThrow()
        )
    }
}
