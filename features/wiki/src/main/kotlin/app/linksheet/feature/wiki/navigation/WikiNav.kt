@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.wiki.navigation

import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.wiki.core.WikiPage
import app.linksheet.feature.wiki.ui.MarkdownViewerWrapper
import fe.composekit.route.Nav
import fe.composekit.route.Route
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Serializable
object WikiNav : Nav {
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<MarkdownViewerRoute> { _, route ->
            val titleStr = route.customTitle?.let { stringResource(id = it) } ?: route.title
            MarkdownViewerWrapper(
                title = titleStr,
                url = route.url,
                rawUrl = route.rawUrl,
                onBackPressed = navController::popBackStack
            )
        }
    }
}


@Keep
@Serializable
data class MarkdownViewerRoute(
    val title: String,
    val url: String,
    val rawUrl: String = url,
    @param:StringRes val customTitle: Int? = null,
) : Route {
    constructor(wikiPage: WikiPage) : this(wikiPage.page, wikiPage.url, wikiPage.rawUrl, wikiPage.customTitle)
}
