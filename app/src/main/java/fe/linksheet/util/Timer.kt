package fe.linksheet.util

class Timer(val start: Long) {

    fun stop(): Long {
        return System.currentTimeMillis() - start
    }

    companion object {
        fun startTimer(): Timer {
            return Timer(System.currentTimeMillis())
        }
    }
}