package fe.linksheet.util

object BitFlagUtil {
    fun or(vararg flags: Int): Int {
        if (flags.isEmpty()) return 0

        var current = flags[0]
        for (flag in flags) current = current or flag

        return current
    }
}
