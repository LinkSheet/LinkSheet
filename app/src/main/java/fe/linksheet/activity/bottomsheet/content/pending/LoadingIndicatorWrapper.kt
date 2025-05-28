package fe.linksheet.activity.bottomsheet.content.pending

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fe.linksheet.module.resolver.ResolveEvent
import fe.linksheet.module.resolver.ResolverInteraction

@Composable
fun LoadingIndicatorWrapper(
    expressiveLoadingSheet: Boolean,
    modifier: Modifier = Modifier,
    event: ResolveEvent,
    interaction: ResolverInteraction,
    requestExpand: () -> Unit,
) {
    if (expressiveLoadingSheet) {
        M3ELoadingIndicatorSheetContent(
            modifier = modifier,
            event = event,
            interaction = interaction,
            requestExpand = requestExpand
        )
    } else {
        LoadingIndicatorSheetContent(
            modifier = modifier,
            event = event,
            interaction = interaction,
            requestExpand = requestExpand
        )
    }
}
