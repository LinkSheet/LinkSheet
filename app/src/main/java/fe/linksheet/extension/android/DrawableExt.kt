package fe.linksheet.extension.android

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import fe.linksheet.util.image.ImageFactory

fun Drawable.toImageBitmap(): ImageBitmap {
    return toBitmap().asImageBitmap()
}

fun Drawable.toBitmap(): Bitmap {
    return ImageFactory.convertToBitmap(this)
}
