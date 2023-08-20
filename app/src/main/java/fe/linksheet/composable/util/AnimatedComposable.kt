package fe.linksheet.composable.util


import android.graphics.Path
import android.view.animation.PathInterpolator
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import fe.android.compose.route.util.ArgumentRoute
import fe.android.compose.route.util.Route
import fe.android.compose.route.util.RouteData
import com.google.accompanist.navigation.animation.composable as animatedComposable

val easing = Easing { f ->
    PathInterpolator(Path().apply {
        moveTo(0f, 0f)
        cubicTo(0.04F, 0F, 0.1F, 0.05F, 0.2F, 0.5F)
        cubicTo(0.20F, 0.8F, 0.2F, 1F, 1F, 1F)
    }).getInterpolation(f)
}

const val tweenDuration = 350
val enterTween = tween<IntOffset>(durationMillis = tweenDuration, easing = easing)
val exitTween = tween<IntOffset>(durationMillis = tweenDuration, easing = easing)

val fadeTween = tween<Float>(durationMillis = 150)

const val initialOffset = 0.10f
val slidePositiveOffset: (fullWidth: Int) -> Int = { (it * initialOffset).toInt() }
val slideNegativeOffset: (fullWidth: Int) -> Int = { -(it * initialOffset).toInt() }

val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
    slideInHorizontally(enterTween, slidePositiveOffset) + fadeIn(fadeTween)
}

val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
    slideOutHorizontally(exitTween, slideNegativeOffset) + fadeOut(fadeTween)
}

val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
    slideInHorizontally(enterTween, slideNegativeOffset) + fadeIn(fadeTween)
}

val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
    slideOutHorizontally(exitTween, slidePositiveOffset) + fadeOut(fadeTween)
}

@OptIn(ExperimentalAnimationApi::class)
fun <T : RouteData, A : Route.Arguments<T, U>, U> NavGraphBuilder.animatedArgumentRouteComposable(
    route: ArgumentRoute<T, A, U>,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry, T) -> Unit
) {
    animatedComposable(
        route.route,
        route.navArguments,
        route.navDeepLinks,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition,
    ) { stack ->
        val bundle = stack.arguments ?: throw IllegalArgumentException("No bundle provided!")
        val data = route.instance(bundle)

        content(stack, data)
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.animatedComposable(
    route: String,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    popEnterTransition = popEnterTransition,
    popExitTransition = popExitTransition,
    content = content
)