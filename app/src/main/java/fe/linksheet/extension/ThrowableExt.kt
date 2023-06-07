package fe.linksheet.extension

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.printToString() = StringWriter().also { sw ->
    PrintWriter(sw).use { printStackTrace(it) }
}.toString()