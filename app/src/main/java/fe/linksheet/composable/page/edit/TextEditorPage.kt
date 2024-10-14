package fe.linksheet.composable.page.edit

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import fe.composekit.component.page.SaneSettingsScaffold
import fe.linksheet.R


private val editorPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorPage(
    source: TextSource = ClipboardCard,
    initialText: String,
    onDoneClicked: (String) -> Unit,
) {
    val view = LocalView.current
    val inputMethodManager = remember(view) { view.context.getSystemService<InputMethodManager>()!! }

    var textState by remember { mutableStateOf(initialText) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    SaneSettingsScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {},
                navigationIcon = {
                    Button(
                        modifier = Modifier.padding(start = editorPadding),
                        onClick = {
                            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                            onDoneClicked(textState)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.generic__text_done))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = editorPadding,
                        start = editorPadding,
                        end = editorPadding
                    )
                    .fillMaxSize()
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = source.id)
                )

                // BasicTextField still doesn't have "native" behavior (https://issuetracker.google.com/issues/137321832)
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        createEditTextView(context, textState) { text, start, count, after ->
                            textState = text.toString()
                        }
                    }
                )
            }
        }
    }
}

sealed class TextSource(@StringRes val id: Int) {

}

data object ClipboardCard : TextSource(R.string.home__clipboard_card_source)

private fun createEditTextView(
    context: Context,
    initialText: String,
    onTextChanged: (CharSequence?, Int, Int, Int) -> Unit,
): EditText {
    val text = EditText(context)

    text.background = null
    text.minHeight = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, context.resources.displayMetrics)
        .toInt()

    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
    text.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    text.gravity = Gravity.TOP
    text.setText(initialText)
    text.addTextChangedListener(onTextChanged = onTextChanged)

    text.requestFocus()

    return text
}


@Preview(apiLevel = 34)
@Composable
private fun TextEditorRoutePreview() {
    TextEditorPage(onDoneClicked = {}, initialText = "Hello fren")
}
