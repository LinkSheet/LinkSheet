package app.linksheet.feature.engine

import app.linksheet.feature.engine.database.EngineDatabase
import app.linksheet.feature.engine.database.repository.CacheRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val LinkEngineFeatureModule = module {
    single<EngineDatabase> {
        EngineDatabase.create(context = get(), name = "engine")
    }
    singleOf(EngineDatabase::htmlCacheDao)
    singleOf(EngineDatabase::previewCacheDao)
    singleOf(EngineDatabase::resolvedUrlCacheDao)
    singleOf(EngineDatabase::resolveTypeDao)
    singleOf(EngineDatabase::urlEntryDao)
    single {
        CacheRepository(get(), get(), get(), get(), get())
    }
}
