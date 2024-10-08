package fe.linksheet.activity.bottomsheet.button

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.column.ClickModifier
import fe.linksheet.activity.bottomsheet.column.ClickType
import fe.linksheet.composable.ui.HkGroteskFontFamily

@Composable
fun ChoiceButtons(
    enabled: Boolean = true,
    choiceClick: (ClickType, ClickModifier) -> Unit,
) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .padding(start = 15.dp, end = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        OpenButton(
            outlined = true,
            enabled = enabled,
            textId = R.string.just_once,
            onClick = { choiceClick(ClickType.Single, ClickModifier.None) })

        OpenButton(
            outlined = false,
            enabled = enabled,
            textId = R.string.always,
            onClick = { choiceClick(ClickType.Single, ClickModifier.Always) })
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
