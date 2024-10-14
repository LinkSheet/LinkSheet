package fe.linksheet.composable.page.home.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.composable.page.edit.TextEditorPage
import fe.linksheet.module.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TextEditorPageWrapper(
    viewModel: MainViewModel = koinViewModel(),
    initialText: String,
    popBackStack: () -> Unit,
) {
    val label = stringResource(id = R.string.generic__text_url)

    TextEditorPage(
        initialText = initialText,
        onDoneClicked = { text ->
            if (text != initialText) {
                viewModel.tryUpdateClipboard(label, text)
            }

            popBackStack()
        },
    )
}
