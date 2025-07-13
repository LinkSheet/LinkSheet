package fe.linksheet.activity

import android.content.Intent
import android.os.Bundle
import fe.composekit.core.getEnumExtra
import fe.composekit.intent.buildIntent
import fe.linksheet.composable.page.edit.TextEditorPage
import fe.linksheet.composable.page.edit.TextSource
import fe.linksheet.composable.page.edit.TextValidator
import fe.linksheet.composable.page.edit.WebUriTextValidator
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.extension.koin.injectLogger
import org.koin.core.component.KoinComponent


class TextEditorActivity : BaseComponentActivity(), KoinComponent {
    private val logger by injectLogger<TextEditorActivity>()

    companion object {
        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_VALIDATOR = "EXTRA_VALIDATOR"
        const val EXTRA_SOURCE = "EXTRA_SOURCE"
    }

    enum class ExtraValidator {
        WebUriTextValidator
    }

    enum class ExtraSource {
        ClipboardCard
    }

    private fun Intent.getSource(default: ExtraSource): TextSource {
        return when (getEnumExtra<ExtraSource>(EXTRA_SOURCE) ?: default) {
            ExtraSource.ClipboardCard -> TextSource.ClipboardCard
        }
    }

    private fun Intent.getValidator(default: ExtraValidator): TextValidator {
        return when (getEnumExtra<ExtraValidator>(EXTRA_VALIDATOR) ?: default) {
            ExtraValidator.WebUriTextValidator -> WebUriTextValidator
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialText = intent.getStringExtra(EXTRA_TEXT) ?: ""

        val source = intent.getSource(ExtraSource.ClipboardCard)
        val validator = intent.getValidator(ExtraValidator.WebUriTextValidator)

        setContent(edgeToEdge = true) {
            AppTheme {
                TextEditorPage(
                    source = source,
                    validator = validator,
                    initialText = initialText,
                    onDone = { text ->
                        val data = buildIntent { putExtra(EXTRA_TEXT, text) }
                        logger.info("onDone: $text, $data")

                        setResult(RESULT_OK, data)
                        finish()
                    },
                    onDismiss = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}
