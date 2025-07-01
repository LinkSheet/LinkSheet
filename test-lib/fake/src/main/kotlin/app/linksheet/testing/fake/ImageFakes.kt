package app.linksheet.testing.fake

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape

object ImageFakes {
    val EmptyDrawable = ShapeDrawable(RectShape()).apply {
        intrinsicWidth = 1
        intrinsicHeight = 1
    }
}
