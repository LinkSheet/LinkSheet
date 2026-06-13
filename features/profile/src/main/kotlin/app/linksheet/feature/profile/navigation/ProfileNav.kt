@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.profile.navigation

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.profile.ui.ProfileSwitchingSettings
import fe.composekit.core.AndroidVersion
import fe.composekit.route.Nav
import fe.composekit.route.Route
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Keep
@Serializable
@SerialName("ProfileNav")
object ProfileNav : Nav {
    override val graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        if (AndroidVersion.isAtLeastApi28P()) {
            animatedComposable<ProfileRoute> { _, route ->
                ProfileSwitchingSettings(onBackPressed = navController::popBackStack)
            }
        }
    }
}


@Keep
@Serializable
@SerialName("ProfileRoute")
data object ProfileRoute : Route {
}
