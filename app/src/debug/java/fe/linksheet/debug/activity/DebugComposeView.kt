package fe.linksheet.debug.activity

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.semantics.*

class DebugComposeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private val content = mutableStateOf<(@Composable () -> Unit)?>(null)

    @Suppress("RedundantVisibilityModifier")
    protected override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    @Composable
    override fun Content() {
        content.value?.invoke()
    }

    override fun getAccessibilityClassName(): CharSequence {
        return javaClass.name
    }

    var semanticsOwner: SemanticsOwner? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("DebugComposeView", "onAttachedToWindow")
        val androidComposeViewClass = Class.forName("androidx.compose.ui.platform.AndroidComposeView")

        val androidComposeView = getChildAt(0)
        Log.d("DebugComposeView", "AndroidComposeView: $androidComposeView")

        val semanticsOwnerField = androidComposeViewClass.getDeclaredField("semanticsOwner")
        semanticsOwnerField.isAccessible = true

        semanticsOwner = semanticsOwnerField.get(androidComposeView) as SemanticsOwner
    }

    /**
     * Set the Jetpack Compose UI content for this view.
     * Initial composition will occur when the view becomes attached to a window or when
     * [createComposition] is called, whichever comes first.
     */
    @OptIn(InternalComposeUiApi::class)
    fun setContent(content: @Composable () -> Unit) {
        shouldCreateCompositionOnAttachedToWindow = true
        this.content.value = content
        if (isAttachedToWindow) {
            createComposition()
            Log.d("DebugComposeView", "SetContent")
            showLayoutBounds = true
        }
    }
}
