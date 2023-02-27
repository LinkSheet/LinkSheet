package com.junkfood.seal.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceSubtitle(
    modifier: Modifier = Modifier,
    text: String,
    paddingStart: Dp = 15.dp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = paddingStart, top = 24.dp, bottom = 12.dp, end = 15.dp),
        color = color,
        style = MaterialTheme.typography.labelLarge
    )
}