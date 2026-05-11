package fe.linksheet.composable.page.home.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.composable.page.edit.TextEditorPage
import fe.linksheet.module.viewmodel.MainViewModel
import org.koin.compose.viewmodel.koinActivityViewModel
import app.linksheet.compose.R as CommonR

@Composable
fun TextEditorPageWrapper(
    viewModel: MainViewModel = koinActivityViewModel(),
    initialText: String,
    popBackStack: () -> Unit,
) {
    val label = stringResource(id = CommonR.string.generic__text_url)

    TextEditorPage(
        initialText = initialText,
        onDone = { text ->
            if (text != initialText) {
                viewModel.clipboardUseCase.tryUpdateClipboard(label, text)
            }

            popBackStack()
        },
        onDismiss = popBackStack
    )
}
