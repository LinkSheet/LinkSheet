package fe.linksheet.util

sealed class Results<out T>(val value: T?) {
    val isSuccess: Boolean get() = this is Success<T>
    val isError: Boolean get() = this is Error<T>
    val isFailure: Boolean get() = this is Failure
    val isLoading: Boolean get() = this is Loading


    data class Success<out T>(val data: T?) : Results<T>(data)
    data class Error<out T>(val data: T?) : Results<T>(data)

    class Failure(exception: Exception) : Results<Exception>(exception)
    class Loading<T>(initialState: T) : Results<T>(initialState)

    companion object {
        fun <T> success(value: T): Results<T> = Success(value)
        fun <T> success(): Results<T> = Success(null)
        fun <T> error(value: T): Results<T> = Error(value)
        fun <T> error(): Results<T> = Error(null)

        fun loading(): Results<Unit> = Loading(Unit)
        fun <T> loading(initialState: T): Results<T> = Loading(initialState)

        fun failure(exception: Exception): Results<Exception> = Failure(exception)

        fun result(boolean: Boolean): Results<Unit> {
            return if (boolean) success() else error()
        }
    }
}