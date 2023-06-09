package fe.linksheet.extension.android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap

fun Drawable.toImageBitmap() = Bitmap.createBitmap(
    intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888
).apply {
    val canvas = Canvas(this)

    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
}.asImageBitmap()
