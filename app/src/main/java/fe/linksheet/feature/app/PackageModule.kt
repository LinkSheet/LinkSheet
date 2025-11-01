package fe.linksheet.feature.app

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.getApplicationInfoCompatOrNull
import android.content.pm.getInstalledPackagesCompat
import android.content.pm.queryIntentActivitiesCompat
import android.content.pm.resolveActivityCompat
import app.linksheet.feature.app.PackageService
import app.linksheet.lib.flavors.LinkSheetApp
import fe.composekit.preference.asFunction
import fe.droidkit.koin.getPackageManager
import fe.droidkit.koin.getSystemServiceOrThrow
import fe.linksheet.BuildConfig
import app.linksheet.feature.app.pkg.DefaultPackageIconLoader
import app.linksheet.feature.app.pkg.DefaultPackageIntentHandler
import app.linksheet.feature.app.pkg.DefaultPackageLabelService
import app.linksheet.feature.app.pkg.DefaultPackageLauncherService
import app.linksheet.feature.app.pkg.PackageIconLoader
import app.linksheet.feature.app.pkg.PackageIntentHandler
import app.linksheet.feature.app.pkg.domain.DomainVerificationManagerCompat
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import org.koin.dsl.module


val AppFeatureModule = module {
    single {
        AndroidPackageIconLoaderModule(
            packageManager = getPackageManager(),
            activityManager = getSystemServiceOrThrow()
        )
    }
    single {
        val experimentRepository = get<ExperimentRepository>()

        AndroidPackageIntentHandler(
            packageManager = getPackageManager(),
            checkReferrerExperiment = experimentRepository.asFunction(Experiments.hideReferrerFromSheet)
        )
    }
    single {
        AndroidPackageServiceModule(context = get(), packageIconLoader = get(), packageIntentHandler = get())
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
internal fun AndroidPackageServiceModule(
    context: Context,
    packageIconLoader: PackageIconLoader,
    packageIntentHandler: PackageIntentHandler,
): PackageService {
    val packageManager = context.packageManager

    return PackageService(
        domainVerificationManager = DomainVerificationManagerCompat(context),
        packageLabelService = DefaultPackageLabelService(
            loadComponentInfoLabelInternal = { it.loadLabel(packageManager) },
            getApplicationLabel = packageManager::getApplicationLabel,
        ),
        packageLauncherService = DefaultPackageLauncherService(packageManager::queryIntentActivitiesCompat),
        packageIconLoader = packageIconLoader,
        packageIntentHandler = packageIntentHandler,
        getApplicationInfoOrNull = packageManager::getApplicationInfoCompatOrNull,
        getInstalledPackages = packageManager::getInstalledPackagesCompat,
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
