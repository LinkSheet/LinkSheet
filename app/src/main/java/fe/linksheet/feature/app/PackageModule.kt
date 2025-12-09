package fe.linksheet.feature.app

import android.app.ActivityManager
import android.content.pm.*
import app.linksheet.feature.app.AppInfoCreator
import app.linksheet.feature.app.pkg.*
import app.linksheet.feature.app.pkg.domain.DomainVerificationManagerCompat
import app.linksheet.feature.app.usecase.AllAppsUseCase
import app.linksheet.feature.app.usecase.BrowsersUseCase
import app.linksheet.feature.app.usecase.DomainVerificationUseCase
import app.linksheet.lib.flavors.LinkSheetApp
import fe.composekit.preference.asFunction
import fe.droidkit.koin.getPackageManager
import fe.droidkit.koin.getSystemServiceOrThrow
import fe.linksheet.BuildConfig
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import org.koin.dsl.module


val AppFeatureModule = module {
    single<PackageIconLoader> {
        AndroidPackageIconLoaderModule(
            packageManager = getPackageManager(),
            activityManager = getSystemServiceOrThrow()
        )
    }
    single<PackageLabelService> {
        val pm = getPackageManager()
        DefaultPackageLabelService(
            loadComponentInfoLabelInternal = { it.loadLabel(pm) },
            getApplicationLabel = pm::getApplicationLabel,
        )
    }
    single<PackageLauncherService> {
        DefaultPackageLauncherService(queryIntentActivities = getPackageManager()::queryIntentActivitiesCompat)
    }
    single<PackageIntentHandler> {
        val experimentRepository = get<ExperimentRepository>()

        AndroidPackageIntentHandler(
            packageManager = getPackageManager(),
            checkReferrerExperiment = experimentRepository.asFunction(Experiments.hideReferrerFromSheet)
        )
    }
    single {
        AppInfoCreator(
            packageLabelService = get(),
            packageLauncherService = get(),
            packageIconLoader = get()
        )
    }
    factory {
        val pm = getPackageManager()
        AllAppsUseCase(
            creator = get(),
            packageIntentHandler = get(),
            getApplicationInfoOrNull = pm::getApplicationInfoCompatOrNull,
            getInstalledPackages = pm::getInstalledPackagesCompat,
        )
    }
    factory {
        BrowsersUseCase(
            creator = get(),
            packageIntentHandler = get(),
        )
    }
    factory {
        val pm = getPackageManager()
        DomainVerificationUseCase(
            creator = get(),
            domainVerificationManager = DomainVerificationManagerCompat(get()),
            packageIntentHandler = get(),
            getApplicationInfoOrNull = pm::getApplicationInfoCompatOrNull,
            getInstalledPackages = pm::getInstalledPackagesCompat,
        )
    }
}


@Suppress("FunctionName")
fun AndroidPackageIconLoaderModule(
    packageManager: PackageManager,
    activityManager: ActivityManager,
): PackageIconLoader {
    val defaultIcon = packageManager.defaultActivityIcon
    val launcherLargeIconDensity = activityManager.launcherLargeIconDensity

    return DefaultPackageIconLoader(
        defaultIcon = defaultIcon,
        getDrawableForDensity = { packageName, resId ->
            packageManager
                .getResourcesForApplication(packageName)
                .getDrawableForDensity(resId, launcherLargeIconDensity, null)
        },
        loadActivityIcon = { it.loadIcon(packageManager) },
    )
}

@Suppress("FunctionName")
internal fun AndroidPackageIntentHandler(
    packageManager: PackageManager,
    checkReferrerExperiment: () -> Boolean,
): PackageIntentHandler {
    return DefaultPackageIntentHandler(
        queryIntentActivities = packageManager::queryIntentActivitiesCompat,
        resolveActivity = packageManager::resolveActivityCompat,
        isLinkSheetCompat = { pkg -> LinkSheetApp.Compat.isApp(pkg) != null },
        isSelf = { pkg -> BuildConfig.APPLICATION_ID == pkg },
        checkReferrerExperiment = checkReferrerExperiment,
    )
}
