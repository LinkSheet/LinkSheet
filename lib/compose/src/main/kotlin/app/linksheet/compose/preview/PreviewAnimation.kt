package app.linksheet.compose.preview

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries

@Composable
inline fun <reified T : Enum<T>> rememberInfiniteEnumTransition(
    entries: EnumEntries<T> = enumEntries<T>(),
    durationPerEntry: Int = 2_000,
    animation: KeyframesSpec<Int> = keyframes {
        durationMillis = entries.size * durationPerEntry
        for (idx in entries.indices) {
            idx at idx * durationPerEntry
        }
    }
): T {
    val transition = rememberInfiniteTransition()
    val idx by transition.animateValue(
        initialValue = 0,
        targetValue = entries.size,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(animation = animation)
    )

    return remember(idx) { entries[idx] }
}
