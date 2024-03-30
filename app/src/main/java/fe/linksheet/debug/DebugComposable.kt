package fe.linksheet.debug

import androidx.compose.runtime.Composer

sealed class DebugComposable(clazzName: String, methodName: String) {
    private val method by lazy {
        Class.forName(clazzName).methods
            .find { it.name == methodName }
            ?.apply { isAccessible = true }
    }

    companion object {
        private const val DEBUG_PACKAGE: String = "fe.linksheet.debug.ui.composable"
    }

    data object MainRoute : DebugComposable("$DEBUG_PACKAGE.DebugMenuKt", "DebugMenu")

    fun compose(currentComposer: Composer, i: Int) {
        method?.invoke(null, currentComposer, i)
    }
}
