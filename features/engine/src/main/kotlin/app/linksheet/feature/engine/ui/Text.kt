package app.linksheet.feature.engine.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun Text12Monospace(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace
    )
}

@Composable
fun Text12(text: String, textAlign: TextAlign? = null) {
    Text(
        text = text,
        fontSize = 12.sp,
        textAlign = textAlign
    )
}
