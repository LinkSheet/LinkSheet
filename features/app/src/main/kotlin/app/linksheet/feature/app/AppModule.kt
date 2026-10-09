package app.linksheet.feature.app

import android.content.pm.getApplicationInfoCompatOrNull
import android.content.pm.getInstalledPackagesCompat
import android.content.pm.queryIntentActivitiesCompat
import app.linksheet.api.SystemInfoService
import app.linksheet.feature.app.core.AppInfoCreator
import app.linksheet.feature.app.core.DefaultManifestParser
import app.linksheet.feature.app.core.DefaultMetaDataHandler
import app.linksheet.feature.app.core.DefaultPackageIconLoader
import app.linksheet.feature.app.core.DefaultPackageIntentHandler
import app.linksheet.feature.app.core.DefaultPackageLabelService
import app.linksheet.feature.app.core.DefaultPackageLauncherService
import app.linksheet.feature.app.core.ManifestParser
import app.linksheet.feature.app.core.MetaDataHandler
import app.linksheet.feature.app.core.PackageIconLoader
import app.linksheet.feature.app.core.PackageIntentHandler
import app.linksheet.feature.app.core.PackageLabelService
import app.linksheet.feature.app.core.PackageLauncherService
import app.linksheet.feature.app.core.domain.DomainVerificationManagerCompat
import app.linksheet.feature.app.usecase.AllAppsUseCase
import app.linksheet.feature.app.usecase.BrowsersUseCase
import app.linksheet.feature.app.usecase.DomainVerificationUseCase
import fe.droidkit.koin.getPackageManager
import org.koin.dsl.module


val AppModule = module {
    single<PackageIconLoader> {
        DefaultPackageIconLoader(context = get())
    }
    single<PackageLabelService> {
        DefaultPackageLabelService(context = get())
    }
    single<PackageLauncherService> {
        DefaultPackageLauncherService(queryIntentActivities = getPackageManager()::queryIntentActivitiesCompat)
    }
    single<PackageIntentHandler> {
        val applicationId = get<SystemInfoService>().getApplicationId()
        DefaultPackageIntentHandler(context = get(), applicationId = applicationId)
    }
    single<DomainVerificationManagerCompat> {
        DomainVerificationManagerCompat(context = get())
    }
    single<ManifestParser> { DefaultManifestParser() }
    single<MetaDataHandler> {
        val applicationId = get<SystemInfoService>().getApplicationId()
        DefaultMetaDataHandler(context = get(), applicationId = applicationId)
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
