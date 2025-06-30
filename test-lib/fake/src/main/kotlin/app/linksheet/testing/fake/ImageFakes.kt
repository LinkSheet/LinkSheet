package app.linksheet.testing.fake

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.compose.ui.graphics.ImageBitmap

object ImageFakes {
    val EmptyDrawable = ShapeDrawable(RectShape()).apply {
        intrinsicWidth = 1
        intrinsicHeight = 1
    }
    val ImageBitmap = lazy { ImageBitmap(1, 1) }
}
