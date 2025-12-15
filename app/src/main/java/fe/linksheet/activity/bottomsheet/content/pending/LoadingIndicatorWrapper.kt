package fe.linksheet.activity.bottomsheet.content.pending

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fe.linksheet.module.resolver.ResolveEvent
import fe.linksheet.module.resolver.ResolverInteraction

@Composable
fun LoadingIndicatorWrapper(
    modifier: Modifier = Modifier,
    event: ResolveEvent,
    interaction: ResolverInteraction,
    requestExpand: () -> Unit,
) {
    M3ELoadingIndicatorSheetContent(
        modifier = modifier,
        event = event,
        interaction = interaction,
        requestExpand = requestExpand
    )
}
