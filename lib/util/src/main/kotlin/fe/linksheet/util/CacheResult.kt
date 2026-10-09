package fe.linksheet.util

sealed interface CacheResult<out T> {
    data object Miss : CacheResult<Nothing>
}
sealed interface StoredCacheResult<out T> : CacheResult<T> {
    val value: T

    data class Hit<T>(override val value: T) : StoredCacheResult<T>
    data class Stale<T>(override val value: T) : StoredCacheResult<T>
}
