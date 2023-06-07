package fe.linksheet.extension


fun <T> failure(msg: String) = Result.failure<T>(Exception(msg))