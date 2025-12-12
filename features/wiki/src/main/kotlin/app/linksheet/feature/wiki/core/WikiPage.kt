package app.linksheet.feature.wiki.core

import androidx.annotation.StringRes

class WikiPage(
    val org: String,
    val repo: String,
    val page: String,
    val id: String? = null,
    @param:StringRes val customTitle: Int? = null
) {
    val url = "https://github.com/$org/$repo/wiki/$page"
    val rawUrl = "https://raw.githubusercontent.com/wiki/$org/$repo/$page.md"
}
