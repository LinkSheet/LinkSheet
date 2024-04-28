package fe.linksheet.experiment.ui.overhaul.icons.outlined

import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp


fun materialIcon2(
    name: String,
    autoMirror: Boolean = false,
    block: ImageVector.Builder.() -> ImageVector.Builder,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 960f,
    viewportHeight = 960f,
    autoMirror = autoMirror
).block().build()

fun test(): ImageVector {
    return materialIcon2(name = "Outlined.DomainVerification") {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1.0f,
            stroke = null,
            strokeAlpha = 1.0f,
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(160.0f, 800.0f)
            quadTo(127.0f, 800.0f, 103.5f, 776.5f)
            quadTo(80.0f, 753.0f, 80.0f, 720.0f)
            lineTo(80.0f, 240.0f)
            quadTo(80.0f, 207.0f, 103.5f, 183.5f)
            quadTo(127.0f, 160.0f, 160.0f, 160.0f)
            lineTo(800.0f, 160.0f)
            quadTo(833.0f, 160.0f, 856.5f, 183.5f)
            quadTo(880.0f, 207.0f, 880.0f, 240.0f)
            lineTo(880.0f, 720.0f)
            quadTo(880.0f, 753.0f, 856.5f, 776.5f)
            quadTo(833.0f, 800.0f, 800.0f, 800.0f)
            lineTo(160.0f, 800.0f)
            close()
            moveTo(160.0f, 720.0f)
            lineTo(800.0f, 720.0f)
            quadTo(800.0f, 720.0f, 800.0f, 720.0f)
            quadTo(800.0f, 720.0f, 800.0f, 720.0f)
            lineTo(800.0f, 320.0f)
            lineTo(160.0f, 320.0f)
            lineTo(160.0f, 720.0f)
            quadTo(160.0f, 720.0f, 160.0f, 720.0f)
            quadTo(160.0f, 720.0f, 160.0f, 720.0f)
            close()
            moveTo(438.0f, 662.0f)
            lineTo(296.0f, 520.0f)
            lineTo(354.0f, 462.0f)
            lineTo(438.0f, 546.0f)
            lineTo(606.0f, 378.0f)
            lineTo(664.0f, 436.0f)
            lineTo(438.0f, 662.0f)
            close()
            moveTo(160.0f, 720.0f)
            lineTo(160.0f, 720.0f)
            quadTo(160.0f, 720.0f, 160.0f, 720.0f)
            quadTo(160.0f, 720.0f, 160.0f, 720.0f)
            lineTo(160.0f, 240.0f)
            quadTo(160.0f, 240.0f, 160.0f, 240.0f)
            quadTo(160.0f, 240.0f, 160.0f, 240.0f)
            lineTo(160.0f, 240.0f)
            quadTo(160.0f, 240.0f, 160.0f, 240.0f)
            quadTo(160.0f, 240.0f, 160.0f, 240.0f)
            lineTo(160.0f, 720.0f)
            quadTo(160.0f, 720.0f, 160.0f, 720.0f)
            quadTo(160.0f, 720.0f, 160.0f, 720.0f)
            close()
        }
//        materialPath {
//            moveTo(16.6f, 10.88f)
//            lineToRelative(-1.42f, -1.42f)
//            lineToRelative(-4.24f, 4.25f)
//            lineToRelative(-2.12f, -2.13f)
//            lineToRelative(-1.42f, 1.42f)
//            lineToRelative(3.54f, 3.54f)
//            close()
//        }
//        materialPath {
//            moveTo(19.0f, 4.0f)
//            horizontalLineTo(5.0f)
//            curveTo(3.89f, 4.0f, 3.0f, 4.9f, 3.0f, 6.0f)
//            verticalLineToRelative(12.0f)
//            curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
//            horizontalLineToRelative(14.0f)
//            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
//            verticalLineTo(6.0f)
//            curveTo(21.0f, 4.9f, 20.11f, 4.0f, 19.0f, 4.0f)
//            close()
//            moveTo(19.0f, 18.0f)
//            horizontalLineTo(5.0f)
//            verticalLineTo(8.0f)
//            horizontalLineToRelative(14.0f)
//            verticalLineTo(18.0f)
//            close()
//        }
    }


}
