package fe.linksheet.experiment.ui.overhaul.composable.util

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

@Stable
interface IconType {
    @Composable
    fun rememberPainter(): Painter
}

@Stable
class ImageVectorIconType private constructor(private val imageVector: ImageVector) : IconType {
    @Composable
    override fun rememberPainter(): Painter = rememberVectorPainter(imageVector)

    companion object {
        fun vector(imageVector: ImageVector): ImageVectorIconType {
            return ImageVectorIconType(imageVector)
        }
    }
}

@Stable
class DrawableIconType private constructor(@DrawableRes private val id: Int) : IconType {

    @Composable
    override fun rememberPainter(): Painter = painterResource(id)

    companion object {
        fun drawable(@DrawableRes id: Int): DrawableIconType {
            return DrawableIconType(id)
        }
    }
}
