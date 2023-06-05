package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun CheckboxRow(
    checked: Boolean,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    ClickableRow(
        verticalAlignment = Alignment.CenterVertically,
        onClick = onClick
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Spacer(modifier = Modifier.width(5.dp))

        content(this)
    }
}

@Composable
fun CheckboxRow(
    checked: Boolean,
    onClick: () -> Unit,
    @StringRes textId: Int
) {
    ClickableRow(
        verticalAlignment = Alignment.CenterVertically,
        onClick = onClick
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onClick() }
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(text = stringResource(id = textId))
    }
}