package fe.linksheet

import android.util.Log
import java.io.PrintStream
import kotlin.reflect.KClass

abstract class Logger(protected val tag: String) {
    enum class Level(val id: String) {
        Debug("D"),
        Info("I"),
        Warn("W"),
        Error("E")
    }

    abstract fun debug(message: () -> String)
    abstract fun info(message: () -> String)
    abstract fun warn(message: () -> String)
    abstract fun error(message: () -> String)
}

class AndroidLogger(tag: String) : Logger(tag) {
    constructor(clazz: KClass<*>) : this(clazz.simpleName!!)

    override fun debug(message: () -> String) {
        Log.d(tag, message())
    }

    override fun info(message: () -> String) {
        Log.i(tag, message())
    }

    override fun warn(message: () -> String) {
        Log.w(tag, message())
    }

    override fun error(message: () -> String) {
        Log.e(tag, message())
    }
}

class PrintLogger(tag: String, private val printer: PrintStream) : Logger(tag) {
    private fun println(level: Level, message: () -> String) {
        printer.println("[$tag] ${level.id}: ${message()}")
    }

    override fun debug(message: () -> String) {
        println(Level.Debug, message)
    }

    override fun info(message: () -> String) {
        println(Level.Info, message)
    }

    override fun warn(message: () -> String) {
        println(Level.Warn, message)
    }

    override fun error(message: () -> String) {
        println(Level.Error, message)
    }
}

@Suppress("FunctionName")
fun DefaultPrintLogger(tag: String): PrintLogger {
    return PrintLogger(tag, System.out)
}

@Suppress("FunctionName")
inline fun <reified T> DefaultPrintLogger(): PrintLogger {
    return DefaultPrintLogger(T::class.simpleName!!)
}

@Suppress("FunctionName")
inline fun <reified T> AndroidLogger(): Logger {
    return AndroidLogger(T::class)
}
