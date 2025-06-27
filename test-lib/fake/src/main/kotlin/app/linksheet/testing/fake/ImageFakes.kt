package app.linksheet.testing.fake

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.compose.ui.graphics.ImageBitmap

object ImageFakes {
    val EmptyDrawable = ShapeDrawable(RectShape())
    val ImageBitmap = lazy { ImageBitmap(1, 1) }
}
