package fe.linksheet.util

sealed interface CacheResult<out T> {
    data class Hit<T>(val value: T) : CacheResult<T>
    data class Stale<T>(val value: T) : CacheResult<T>
    data object Miss : CacheResult<Nothing>
}
