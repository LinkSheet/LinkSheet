package fe.linksheet.compose.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

sealed interface PaddingValuesSides {
    fun only(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues
    fun exclude(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues

    data object Horizontal : PaddingValuesSides {
        override fun only(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection)
            )
        }

        override fun exclude(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return Vertical.only(paddingValues, layoutDirection)
        }
    }

    data object Vertical : PaddingValuesSides {
        override fun only(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        }

        override fun exclude(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return Horizontal.only(paddingValues, layoutDirection)
        }
    }

    data object Start : PaddingValuesSides {
        override fun only(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(start = paddingValues.calculateStartPadding(layoutDirection))
        }

        override fun exclude(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(
                top = paddingValues.calculateTopPadding(),
                end = paddingValues.calculateEndPadding(layoutDirection),
                bottom = paddingValues.calculateBottomPadding()
            )
        }
    }

    data object End : PaddingValuesSides {
        override fun only(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(end = paddingValues.calculateEndPadding(layoutDirection))
        }

        override fun exclude(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(
                start = paddingValues.calculateStartPadding(layoutDirection),
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        }
    }

    data object Top : PaddingValuesSides {
        override fun only(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(top = paddingValues.calculateTopPadding())
        }

        override fun exclude(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection),
                bottom = paddingValues.calculateBottomPadding()
            )
        }
    }

    data object Bottom : PaddingValuesSides {
        override fun only(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(bottom = paddingValues.calculateBottomPadding())
        }

        override fun exclude(paddingValues: PaddingValues, layoutDirection: LayoutDirection): PaddingValues {
            return PaddingValues(
                start = paddingValues.calculateStartPadding(layoutDirection),
                top = paddingValues.calculateTopPadding(),
                end = paddingValues.calculateEndPadding(layoutDirection),
            )
        }
    }
}

@Composable
fun PaddingValues.exclude(sides: PaddingValuesSides): PaddingValues {
    return exclude(sides = sides, LocalLayoutDirection.current)
}

fun PaddingValues.exclude(sides: PaddingValuesSides, layoutDirection: LayoutDirection): PaddingValues {
    return sides.exclude(this, layoutDirection)
}

@Composable
fun PaddingValues.only(sides: PaddingValuesSides): PaddingValues {
    return only(sides = sides, LocalLayoutDirection.current)
}

fun PaddingValues.only(sides: PaddingValuesSides, layoutDirection: LayoutDirection): PaddingValues {
    return sides.only(this, layoutDirection)
}
