package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.ClipboardManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import fe.composekit.extension.getFirstText
import fe.composekit.extension.getSystemServiceOrThrow
import fe.composekit.preference.asFunction
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.util.intent.parser.IntentParser
import fe.std.result.StdResult
import fe.std.result.unaryPlus

class ClipboardProxyViewModel(
    val context: Application,
    experimentRepository: ExperimentRepository,
) : ViewModel() {
    private val clipboardManager = context.getSystemServiceOrThrow<ClipboardManager>()
    private val parseTextUrlsToHttps =
        experimentRepository.asFunction(Experiments.parseTextUrlsToHttps)

    fun handleClipboard(): StdResult<Uri> {
        val text = clipboardManager.getFirstText() ?: return +ClipboardProxyException.NoText()
        val result = IntentParser.parseText(text, parseTextUrlsToHttps())
        return result
    }
}

sealed class ClipboardProxyException : Exception() {
    class NoText : ClipboardProxyException()
}
