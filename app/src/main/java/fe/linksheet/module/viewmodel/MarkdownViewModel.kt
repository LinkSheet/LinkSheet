package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import fe.httpkt.Request
import fe.linksheet.feature.wiki.WikiArticleUseCase
import fe.linksheet.module.repository.WikiCacheRepository

class MarkdownViewModel(
    val context: Application,
    val request: Request,
    val repository: WikiCacheRepository,
) : ViewModel() {
    private val useCase = WikiArticleUseCase(request, repository)
    suspend fun getWikiText(url: String): String? = useCase.getWikiText(url)
}
