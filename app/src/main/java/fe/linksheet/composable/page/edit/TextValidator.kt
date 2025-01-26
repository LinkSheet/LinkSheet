package fe.linksheet.composable.page.edit

import fe.linksheet.util.web.UriUtil


interface TextValidator {
    fun isValid(text: String): Boolean
}

object WebUriTextValidator : TextValidator {
    override fun isValid(text: String): Boolean {
        return UriUtil.parseWebUriStrict(text) != null
    }
}
