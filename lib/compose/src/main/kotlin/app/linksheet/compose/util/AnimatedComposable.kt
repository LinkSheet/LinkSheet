package app.linksheet.compose.util


import android.graphics.Path
import android.view.animation.PathInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import fe.composekit.route.NavTypes
import fe.composekit.route.Route
import androidx.navigation.compose.composable as navigationComposable

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

fun NavGraphBuilder.animatedComposable(
    route: String,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) = navigationComposable(
    route,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    popEnterTransition = popEnterTransition,
    popExitTransition = popExitTransition,
    content = content
)

inline fun <reified R : Route> NavGraphBuilder.animatedComposable(
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry, R) -> Unit,
) {
    navigationComposable<R>(
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        typeMap = NavTypes.Types,
    ) { stack ->
        content(stack, stack.toRoute<R>())
    }
}
