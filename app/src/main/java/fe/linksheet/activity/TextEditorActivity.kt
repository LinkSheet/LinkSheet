package fe.linksheet.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import fe.composekit.core.getEnumExtra
import fe.composekit.intent.buildIntent
import fe.linksheet.TextValidator
import fe.linksheet.Validator
import fe.linksheet.WebUriTextValidator
import fe.linksheet.composable.page.edit.TextEditorPage
import fe.linksheet.composable.page.edit.TextSource
import fe.linksheet.composable.ui.AppTheme
import mozilla.components.support.base.log.logger.Logger
import org.koin.core.component.KoinComponent


class TextEditorActivity : BaseComponentActivity(), KoinComponent {
    private val logger = Logger("TextEditorActivity")

    companion object {
        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_VALIDATOR = "EXTRA_VALIDATOR"
        const val EXTRA_SOURCE = "EXTRA_SOURCE"
    }

    enum class ExtraSource {
        ClipboardCard
    }

    private fun Intent.getSource(default: ExtraSource): TextSource {
        return when (getEnumExtra<ExtraSource>(EXTRA_SOURCE) ?: default) {
            ExtraSource.ClipboardCard -> TextSource.ClipboardCard
        }
    }

    private fun Intent.getValidator(default: Validator): TextValidator<Uri> {
        return when (getEnumExtra<Validator>(EXTRA_VALIDATOR) ?: default) {
            Validator.WebUriTextValidator -> WebUriTextValidator
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialText = intent.getStringExtra(EXTRA_TEXT) ?: ""

        val source = intent.getSource(ExtraSource.ClipboardCard)
        val validator = intent.getValidator(Validator.WebUriTextValidator)

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
