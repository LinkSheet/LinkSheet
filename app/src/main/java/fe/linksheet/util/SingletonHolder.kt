package fe.linksheet.util

/**
 * Taken from https://stackoverflow.com/a/53580852
 */
open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(): T {
        instance?.let {
            return it
        } ?: throw Exception("Singleton $constructor has not been initialized yet")
    }

    fun createInstance(arg: A): T {
        return when {
            instance != null -> instance!!
            else -> synchronized(this) {
                if (instance == null) instance = constructor(arg)
                instance!!
            }
        }
    }
}
