package fe.linksheet.util

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.test.core.app.ActivityScenario
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


internal fun <A : ComponentActivity> ActivityScenario<A>.getActivity(): A? {
    var activity: A? = null
    onActivity { activity = it }
    return activity
}

@ExperimentalTestApi
fun <A : ComponentActivity> runAndroidComposeUiTest(
    activityLauncher: () -> ActivityScenario<A>,
    effectContext: CoroutineContext = EmptyCoroutineContext,
    block: AndroidComposeUiTest<A>.(ActivityScenario<A>) -> Unit
) {
    var scenario: ActivityScenario<A>? = null
    val environment = AndroidComposeUiTestEnvironment<A>(effectContext) {
        requireNotNull(scenario) {
            "ActivityScenario has not yet been launched, or has already finished. Make sure that " +
                    "any call to ComposeUiTest.setContent() and AndroidComposeUiTest.getActivity() " +
                    "is made within the lambda passed to AndroidComposeUiTestEnvironment.runTest()"
        }.getActivity()
    }
    try {
        environment.runTest {
            scenario = activityLauncher()
            block(scenario)
        }
    } finally {
        scenario?.close()
    }
}
