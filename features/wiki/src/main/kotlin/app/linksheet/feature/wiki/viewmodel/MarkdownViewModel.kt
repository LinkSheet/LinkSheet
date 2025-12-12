package app.linksheet.feature.wiki.viewmodel

import androidx.lifecycle.ViewModel
import app.linksheet.feature.wiki.usecase.WikiArticleUseCase

class MarkdownViewModel internal constructor(
    private val useCase: WikiArticleUseCase
) : ViewModel() {
    suspend fun getWikiText(url: String): String? = useCase.getWikiText(url)
}
