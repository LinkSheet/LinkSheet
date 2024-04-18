package fe.linksheet.debug.activity

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.SemanticsOwner
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getAllSemanticsNodes
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import fe.linksheet.debug.ui.layout.ComposableDebugBoxLayout
import fe.linksheet.activity.bottomsheet.UrlCard
import fe.linksheet.ui.AppTheme
import me.saket.unfurl.UnfurlResult
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class ComposableRendererActivity : ComponentActivity() {
    private lateinit var debugComposeView: DebugComposeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        debugComposeView = DebugComposeView(this)
        debugComposeView.setParentCompositionContext(null)
        debugComposeView.setContent { Host() }

        setOwners()
        setContentView(
            debugComposeView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun ComponentActivity.setOwners() {
        val decorView = window.decorView
        if (decorView.findViewTreeLifecycleOwner() == null) {
            decorView.setViewTreeLifecycleOwner(this)
        }
        if (decorView.findViewTreeViewModelStoreOwner() == null) {
            decorView.setViewTreeViewModelStoreOwner(this)
        }
        if (decorView.findViewTreeSavedStateRegistryOwner() == null) {
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
    }

    fun logNodes(semanticsOwner: SemanticsOwner): MutableList<Overlay> {
        val nodes = semanticsOwner.getAllSemanticsNodes(mergingEnabled = false)
        Log.d("DebugComposeView", "Found ${nodes.size} semanticsNodes")

        val overlays = mutableListOf<Overlay>()

        for (node in nodes) {
            val tag = node.config.getOrNull(SemanticsProperties.TestTag)
            if (tag != null) {
                Log.d("DebugComposeView", "${node.layoutInfo} has tag $tag")
                overlays.add(Overlay(node.positionInRoot, node.layoutInfo.width, node.layoutInfo.height, tag))
            }
        }

        return overlays
    }

    @Stable
    data class Overlay(val positionInRoot: Offset, val width: Int, val height: Int, val tag: String) {
        val text = "$tag: ${width}x${height}"
    }

    @Composable
    fun RendererHost(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
        AppTheme {
            Surface(color = MaterialTheme.colorScheme.surface) {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .then(modifier),
//                ) {
//
//                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(modifier), content = content
                )
            }
        }
    }


    @Composable
    fun Host() {
        val textMeasurer = rememberTextMeasurer()
        val overlays = remember { mutableStateListOf<Overlay>() }

        RendererHost(modifier = Modifier.drawWithContent {
            drawContent()

            overlays.forEach { overlay ->
                val measuredText = textMeasurer.measure(
                    text = overlay.text,
                    style = TextStyle(fontSize = 14.sp, color = Color.White)
                )

                drawRect(color = Color.Black, topLeft = overlay.positionInRoot, size = measuredText.size.toSize())
                drawText(textLayoutResult = measuredText, topLeft = overlay.positionInRoot)
            }
        }) {
            FilledTonalButton(modifier = Modifier.padding(horizontal = 10.dp), onClick = {
                val newList = logNodes(debugComposeView.semanticsOwner!!)
                overlays.addAll(newList)
            }) {
                Text(text = "Find test tags")
            }

            Spacer(modifier = Modifier.height(5.dp))

            ComposableDebugBoxLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
                    .dashedBorder(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        shape = RectangleShape
                    )
            ) {
                UrlCard(
                    uri = "https://www.youtube.com/watch?v=DEhphcTaVxM",
                    unfurlResult = UnfurlResult(
                        url = "https://www.youtube.com/watch?v=DEhphcTaVxM".toHttpUrlOrNull()!!,
                        description = "Skip the waitlist and invest in blue-chip art for the very first time by signing up for Masterworks: https://www.masterworks.art/moonPurchase shares in great...",
                        title = "What Happens When China Invades America?",
                        favicon = "https://www.youtube.com/s/desktop/accca349/img/favicon.ico".toHttpUrlOrNull(),
                        thumbnail = "https://i.ytimg.com/vi/DEhphcTaVxM/maxresdefault.jpg".toHttpUrlOrNull(),
                    )
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            ComposableDebugBoxLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
                    .dashedBorder(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        shape = RectangleShape
                    )
            ) {
                UrlCard(
                    uri = "https://f-droid.org/packages/me.ash.reader",
                    unfurlResult = UnfurlResult(
                        url = "https://f-droid.org/packages/me.ash.reader".toHttpUrlOrNull()!!,
                        description = "A modern and elegant RSS reader with Material You design",
                        title = "Read You | F-Droid - Free and Open Source Android App Repository",
                        favicon = "https://f-droid.org/assets/apple-touch-icon_ypJwtCrcixeH_qV6LdcMYk1anFIR9o-_ufR__1wNdJY=.png".toHttpUrlOrNull(),
                        thumbnail = "https://f-droid.org/repo/me.ash.reader/en-US/icon_Bq9nQb_UzmI1DBSluSA8Q-d5tFo9dQWmyIyt69onHfo=.png".toHttpUrlOrNull(),
                    )
                )
            }
        }
    }

    // https://medium.com/@kappdev/dashed-borders-in-jetpack-compose-a-comprehensive-guide-de990a944c4c
    private fun Modifier.dashedBorder(
        color: Color,
        shape: Shape,
        strokeWidth: Dp = 2.dp,
        dashWidth: Dp = 4.dp,
        gapWidth: Dp = 4.dp,
        cap: StrokeCap = StrokeCap.Round,
    ) = drawWithContent {
        val outline = shape.createOutline(size, layoutDirection, this)

        val path = Path()
        path.addOutline(outline)

        val stroke = Stroke(
            cap = cap,
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashWidth.toPx(), gapWidth.toPx()),
                phase = 0f
            )
        )

        this.drawContent()

        drawPath(
            path = path,
            style = stroke,
            color = color
        )
    }
}
