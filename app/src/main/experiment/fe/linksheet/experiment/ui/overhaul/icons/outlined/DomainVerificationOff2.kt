import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DomainVerification
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R


@Preview
@Composable
private fun VectorPreview() {
    Column(modifier = Modifier.background(Color.White)) {
//        Image(imageVector = DomainVerificationOffFill0Wght400Grad0Opsz24, contentDescription = null)
//        Image(imageVector = Icons.Outlined.DomainVerificationOff, null)
        Icon(imageVector = Icons.Outlined.DomainVerification, null)
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.domain_verification_24),
            contentDescription = null
        )
//        Image(imageVector = test(), null)
//        Image(
//            imageVector = ImageVector.vectorResource(id = R.drawable.domain_verification_off_24),
//            contentDescription = null
//        )
    }
}

private var _DomainVerificationOffFill0Wght400Grad0Opsz24: ImageVector? = null

public val DomainVerificationOffFill0Wght400Grad0Opsz24: ImageVector
    get() {
        if (_DomainVerificationOffFill0Wght400Grad0Opsz24 != null) {
            return _DomainVerificationOffFill0Wght400Grad0Opsz24!!
        }
        _DomainVerificationOffFill0Wght400Grad0Opsz24 = ImageVector.Builder(
            name = "DomainVerificationOffFill0Wght400Grad0Opsz24",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
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
                moveTo(818.0f, 932.0f)
                lineTo(686.0f, 800.0f)
                lineTo(160.0f, 800.0f)
                quadTo(127.0f, 800.0f, 103.5f, 776.5f)
                quadTo(80.0f, 753.0f, 80.0f, 720.0f)
                lineTo(80.0f, 240.0f)
                quadTo(80.0f, 207.0f, 103.5f, 183.5f)
                quadTo(127.0f, 160.0f, 160.0f, 160.0f)
                lineTo(160.0f, 160.0f)
                lineTo(160.0f, 274.0f)
                lineTo(26.0f, 140.0f)
                lineTo(83.0f, 83.0f)
                lineTo(875.0f, 875.0f)
                lineTo(818.0f, 932.0f)
                close()
                moveTo(160.0f, 720.0f)
                lineTo(606.0f, 720.0f)
                lineTo(206.0f, 320.0f)
                lineTo(160.0f, 320.0f)
                lineTo(160.0f, 720.0f)
                quadTo(160.0f, 720.0f, 160.0f, 720.0f)
                quadTo(160.0f, 720.0f, 160.0f, 720.0f)
                close()
                moveTo(871.0f, 757.0f)
                lineTo(800.0f, 686.0f)
                lineTo(800.0f, 320.0f)
                lineTo(434.0f, 320.0f)
                lineTo(274.0f, 160.0f)
                lineTo(800.0f, 160.0f)
                quadTo(833.0f, 160.0f, 856.5f, 183.5f)
                quadTo(880.0f, 207.0f, 880.0f, 240.0f)
                lineTo(880.0f, 720.0f)
                quadTo(880.0f, 730.0f, 878.0f, 739.5f)
                quadTo(876.0f, 749.0f, 871.0f, 757.0f)
                close()
                moveTo(607.0f, 493.0f)
                lineTo(549.0f, 435.0f)
                lineTo(606.0f, 378.0f)
                lineTo(664.0f, 436.0f)
                lineTo(607.0f, 493.0f)
                close()
                moveTo(550.0f, 550.0f)
                lineTo(438.0f, 662.0f)
                lineTo(296.0f, 520.0f)
                lineTo(354.0f, 462.0f)
                lineTo(438.0f, 546.0f)
                lineTo(492.0f, 492.0f)
                lineTo(550.0f, 550.0f)
                close()
            }
        }.build()
        return _DomainVerificationOffFill0Wght400Grad0Opsz24!!
    }
