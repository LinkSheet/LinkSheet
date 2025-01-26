package fe.linksheet.util

import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.jvm.java

object ActivityInvoker {
    val instrumentation: Instrumentation by lazy { InstrumentationRegistry.getInstrumentation() }

    inline fun <reified A : Activity> makeMainActivityIntent(context: Context, block: Intent.() -> Unit = {}): Intent {
        return Intent.makeMainActivity(ComponentName(context, A::class.java)).apply(block)
    }

    inline fun <reified A : Activity> getIntentForActivity(block: Intent.() -> Unit = {}): Intent {
        val intent = makeMainActivityIntent<A>(instrumentation.targetContext, block)
        return when {
            instrumentation.targetContext.packageManager.resolveActivity(intent, 0) != null -> {
                intent
            }

            else -> makeMainActivityIntent<A>(instrumentation.context, block)
        }
    }
}
