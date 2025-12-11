package fe.linksheet.feature.app

import android.app.ActivityManager
import android.content.pm.getApplicationInfoCompatOrNull
import android.content.pm.getInstalledPackagesCompat
import android.content.pm.queryIntentActivitiesCompat
import android.content.pm.resolveActivityCompat
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
        val pm = getPackageManager()
        val launcherLargeIconDensity = getSystemServiceOrThrow<ActivityManager>().launcherLargeIconDensity

        DefaultPackageIconLoader(
            defaultIcon = pm.defaultActivityIcon,
            getDrawableForDensity = { packageName, resId ->
                pm.getResourcesForApplication(packageName).getDrawableForDensity(resId, launcherLargeIconDensity, null)
            },
            loadActivityIcon = { it.loadIcon(pm) },
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

        val pm = getPackageManager()
        DefaultPackageIntentHandler(
            queryIntentActivities = pm::queryIntentActivitiesCompat,
            resolveActivity = pm::resolveActivityCompat,
            isLinkSheetCompat = { LinkSheetApp.Compat.isApp(it) != null },
            isSelf = { BuildConfig.APPLICATION_ID == it },
            checkReferrerExperiment = experimentRepository.asFunction(Experiments.hideReferrerFromSheet)
        )
    }
    single<DomainVerificationManagerCompat> {
        DomainVerificationManagerCompat(context = get())
    }
    single<ManifestParser> { ManifestParser() }
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
            manifestParser = get(),
            domainVerificationManager = get(),
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
            domainVerificationManager = get(),
            packageIntentHandler = get(),
            getApplicationInfoOrNull = pm::getApplicationInfoCompatOrNull,
            getInstalledPackages = pm::getInstalledPackagesCompat,
        )
    }
}
