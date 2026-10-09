package fe.linksheet

import android.net.Uri
import fe.linksheet.web.UriUtil


interface TextValidator<R> {
    fun isValid(text: String): Boolean
    fun validate(text: String): R?
}

object WebUriTextValidator : TextValidator<Uri> {
    override fun isValid(text: String): Boolean {
        return validate(text) != null
    }

    override fun validate(text: String): Uri? {
        return UriUtil.parseWebUriStrict(text)
    }
}

enum class Validator {
    WebUriTextValidator
}
