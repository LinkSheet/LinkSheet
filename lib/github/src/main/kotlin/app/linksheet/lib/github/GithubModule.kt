package app.linksheet.lib.github

import org.koin.dsl.module


val GithubModule = module {
    factory {
        GithubClient(client = get())
    }
}

