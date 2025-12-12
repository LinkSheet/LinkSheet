@file:OptIn(ExperimentalTime::class)

package app.linksheet.feature.wiki

import app.linksheet.feature.wiki.database.WikiDatabase
import app.linksheet.feature.wiki.database.repository.WikiCacheRepository
import app.linksheet.feature.wiki.usecase.WikiArticleUseCase
import app.linksheet.feature.wiki.viewmodel.MarkdownViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

val WikiFeatureModule = module {
    single<WikiDatabase> { WikiDatabase.create(context = get(), name = "wiki") }
    viewModelOf(::MarkdownViewModel)
    factory {
        WikiCacheRepository(dao = get<WikiDatabase>().wikiCacheDao(), clock = get())
    }
    factory {
        WikiArticleUseCase(
            client = get(),
            repository = get(),
            dispatcher = Dispatchers.IO
        )
    }
}
