package fe.linksheet.composable.page.edit

import android.net.Uri
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.addTextChangedListener
import fe.android.compose.system.rememberSystemService
import fe.composekit.component.page.SaneSettingsScaffold
import fe.linksheet.R
import fe.linksheet.TextValidator
import fe.linksheet.WebUriTextValidator
import app.linksheet.compose.R as CommonR


private val editorPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorPage(
    source: TextSource = TextSource.ClipboardCard,
    validator: TextValidator<Uri> = WebUriTextValidator,
    initialText: String,
    onDone: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val view = LocalView.current
    val inputMethodManager = rememberSystemService<InputMethodManager>()

    var text by rememberSaveable { mutableStateOf(initialText) }
    val isValid = rememberSaveable(text) { validator.isValid(text) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    SaneSettingsScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
        topBar = {
            EditorAppBar(
                enabled = isValid,
                scrollBehavior = scrollBehavior,
                onDone = {
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    onDone(text)
                },
                onDismiss = onDismiss
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Editor(
                source = source,
                initialText = text,
                onTextChanged = { text = it }
            )
        }
    }
}

const val EDITOR_APP_BAR_DONE_TEST_TAG = "editor_app_bar__done_test_tag"
const val EDITOR_APP_BAR_CANCEL_TEST_TAG = "editor_app_bar__cancel_test_tag"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorAppBar(
    enabled: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    onDone: () -> Unit,
    onDismiss: () -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        scrollBehavior = scrollBehavior,
        title = {},
        navigationIcon = {
            Row(
                modifier = Modifier.padding(horizontal = editorPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier.testTag(EDITOR_APP_BAR_DONE_TEST_TAG),
                    enabled = enabled, onClick = onDone
                ) {
                    Text(text = stringResource(id = R.string.generic__button_text_done))
                }

                TextButton(
                    modifier = Modifier.testTag(EDITOR_APP_BAR_CANCEL_TEST_TAG),
                    onClick = onDismiss
                ) {
                    Text(text = stringResource(id = CommonR.string.generic__button_text_cancel))
                }
            }
        }
    )
}

@Composable
private fun Editor(source: TextSource, initialText: String, onTextChanged: (String) -> Unit) {
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
                EditText(context).apply {
                    background = null
                    minHeight = TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, context.resources.displayMetrics)
                        .toInt()

                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    gravity = Gravity.TOP
                    requestFocus()

                    setText(initialText)
                    addTextChangedListener(onTextChanged = { text, start, count, after ->
                        onTextChanged(text.toString())
                    })
                }
            },
            update = { view ->

            }
        )
    }
}


@Preview(apiLevel = 34)
@Composable
private fun TextEditorRoutePreview() {
    TextEditorPage(onDone = {}, onDismiss = {}, initialText = "Hello fren")
}
