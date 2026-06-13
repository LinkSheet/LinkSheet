@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.browser.navigation

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.browser.ui.PrivateBrowsingBrowsersSettings
import app.linksheet.feature.browser.ui.PrivateBrowsingSettings
import fe.composekit.route.NavSubGraph
import fe.composekit.route.Route
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Keep
@Serializable
@SerialName("PrivateBrowsingNavSubGraph")
object PrivateBrowsingNavSubGraph: NavSubGraph<PrivateBrowsingRoute> {
    override val startDestination = PrivateBrowsingRoute
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        animatedComposable<PrivateBrowsingRoute> { _, route ->
            PrivateBrowsingSettings(onBackPressed = navController::popBackStack, navigate = navController::navigate)
        }

        animatedComposable<PrivateBrowserBrowserRoute> { _, route ->
            PrivateBrowsingBrowsersSettings(onBackPressed = navController::popBackStack)
        }
    }
}


@Keep
@Serializable
@SerialName("PrivateBrowsingRoute")
data object PrivateBrowsingRoute : Route

@Keep
@Serializable
@SerialName("PrivateBrowserBrowserRoute")
data object PrivateBrowserBrowserRoute : Route
