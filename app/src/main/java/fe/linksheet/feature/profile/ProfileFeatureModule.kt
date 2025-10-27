package fe.linksheet.feature.profile

import fe.droidkit.koin.getResources
import fe.droidkit.koin.getSystemServiceOrThrow
import fe.linksheet.R.string
import org.koin.dsl.module

val ProfileFeatureModule = module {
    single {
        CrossProfileAppsCompat(context = get())
    }
    single {
        AndroidProfileSwitcher(
            appLabel = getResources().getString(string.app_name),
            crossProfileAppsCompat = get(),
            userManager = getSystemServiceOrThrow()
        )
    }
}
