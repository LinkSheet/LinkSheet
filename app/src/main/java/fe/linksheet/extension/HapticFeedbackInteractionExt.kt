package fe.linksheet.extension

import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.HapticFeedbackInteraction
import fe.android.span.helper.LinkTags

fun HapticFeedbackInteraction.openUri(tags: LinkTags, id: String, type: FeedbackType) {
    val uri = tags.getById(id) ?: return
    openUri(uri, type)
}
