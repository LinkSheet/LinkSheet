package fe.linksheet.extension.android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun Drawable.toImageBitmap(): ImageBitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    return bitmap.apply {
        val canvas = Canvas(this)

        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
    }.asImageBitmap()
}
