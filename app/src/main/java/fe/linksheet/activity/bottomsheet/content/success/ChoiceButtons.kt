package fe.linksheet.activity.bottomsheet.content.success

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.api.BOTTOM_SHEET_ALWAYS_TEST_TAG
import app.linksheet.api.BOTTOM_SHEET_JUST_ONCE_TEST_TAG
import app.linksheet.compose.theme.HkGroteskFontFamily
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType

@Composable
fun ChoiceButtons(
    enabled: Boolean = true,
    choiceClick: (ClickType, ClickModifier) -> Unit,
) {
    Row(
        modifier = Modifier
            .heightIn(min = ButtonDefaults.MinHeight)
            .padding(start = 15.dp, end = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        OpenButton(
            outlined = true,
            testTag = BOTTOM_SHEET_JUST_ONCE_TEST_TAG,
            enabled = enabled,
            textId = R.string.just_once,
            onClick = { choiceClick(ClickType.Single, ClickModifier.None) }
        )

        OpenButton(
            outlined = false,
            testTag = BOTTOM_SHEET_ALWAYS_TEST_TAG,
            enabled = enabled,
            textId = R.string.always,
            onClick = { choiceClick(ClickType.Single, ClickModifier.Always) }
        )
    }
}

@Composable
private fun RowScope.OpenButton(
    @StringRes textId: Int,
    outlined: Boolean,
    testTag: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val modifier = Modifier
        .testTag(testTag)
        .fillMaxWidth()
        .weight(0.5f)
    val content: @Composable RowScope.() -> Unit = {
        Text(
            text = stringResource(id = textId),
            fontFamily = HkGroteskFontFamily,
            maxLines = 1,
            fontWeight = FontWeight.SemiBold
        )
    }

    if (outlined) {
        FilledTonalButton(modifier = modifier, enabled = enabled, onClick = onClick, content = content)
    } else {
        Button(modifier = modifier, enabled = enabled, onClick = onClick, content = content)
    }
}

@Preview(showBackground = true)
@Composable
private fun ChoiceButtonsPreview() {
    ChoiceButtons(
        choiceClick = { _, _ -> }
    )
}

@Preview(showBackground = true, heightDp = 60)
@Composable
private fun ChoiceButtonsPreview_LargeContainer() {
    ChoiceButtons(
        choiceClick = { _, _ -> }
    )
}

@Preview(showBackground = true, heightDp = 20)
@Composable
private fun ChoiceButtonsPreview_SmallContainer() {
    ChoiceButtons(
        choiceClick = { _, _ -> }
    )
}
