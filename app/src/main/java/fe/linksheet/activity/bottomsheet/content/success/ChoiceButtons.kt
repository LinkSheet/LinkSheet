package fe.linksheet.activity.bottomsheet.content.success

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import app.linksheet.compose.theme.HkGroteskFontFamily

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
            enabled = enabled,
            textId = R.string.just_once,
            onClick = { choiceClick(ClickType.Single, ClickModifier.None) }
        )

        OpenButton(
            outlined = false,
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
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val modifier = Modifier
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
