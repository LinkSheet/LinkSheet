/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package relocated.androidx.compose.material3

import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.window.BackEvent
import android.window.OnBackAnimationCallback
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogWindowProvider
import relocated.androidx.compose.ui.window.SecureFlagPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import relocated.androidx.compose.material3.internal.PredictiveBack

// Logic forked from androidx.compose.ui.window.DialogProperties. Removed dismissOnClickOutside
// and usePlatformDefaultWidth as they are not relevant for fullscreen experience.
/**
 * Properties used to customize the behavior of a [ModalBottomSheet].
 *
 * @param securePolicy Policy for setting [WindowManager.LayoutParams.FLAG_SECURE] on the bottom
 *   sheet's window.
 * @param shouldDismissOnBackPress Whether the modal bottom sheet can be dismissed by pressing the
 *   back button. If true, pressing the back button will call onDismissRequest.
 * @param isAppearanceLightStatusBars If true, changes the foreground color of the status bars to
 *   light so that the items on the bar can be read clearly. If false, reverts to the default
 *   appearance.
 * @param isAppearanceLightNavigationBars If true, changes the foreground color of the navigation
 *   bars to light so that the items on the bar can be read clearly. If false, reverts to the
 *   default appearance.
 */
@Immutable
@ExperimentalMaterial3Api
class ModalBottomSheetProperties(
    val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    actual val shouldDismissOnBackPress: Boolean = true,
    actual val isAppearanceLightStatusBars: Boolean = true,
    actual val isAppearanceLightNavigationBars: Boolean = true,
) {
    constructor(
        shouldDismissOnBackPress: Boolean,
        isAppearanceLightStatusBars: Boolean,
        isAppearanceLightNavigationBars: Boolean,
    ) : this(
        securePolicy = SecureFlagPolicy.Inherit,
        shouldDismissOnBackPress = shouldDismissOnBackPress,
        isAppearanceLightStatusBars = isAppearanceLightStatusBars,
        isAppearanceLightNavigationBars = isAppearanceLightNavigationBars
    )

    @Deprecated(
        message = "'isFocusable' param is no longer used. Use constructor without this parameter.",
        level = DeprecationLevel.WARNING,
        replaceWith =
            ReplaceWith("ModalBottomSheetProperties(securePolicy, shouldDismissOnBackPress)")
    )
    @Suppress("UNUSED_PARAMETER")
    constructor(
        securePolicy: SecureFlagPolicy,
        isFocusable: Boolean,
        shouldDismissOnBackPress: Boolean,
    ) : this(
        securePolicy = securePolicy,
        shouldDismissOnBackPress = shouldDismissOnBackPress,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModalBottomSheetProperties) return false
        if (securePolicy != other.securePolicy) return false
        if (isAppearanceLightStatusBars != other.isAppearanceLightStatusBars) return false
        if (isAppearanceLightNavigationBars != other.isAppearanceLightNavigationBars) return false

        return true
    }

    override fun hashCode(): Int {
        var result = securePolicy.hashCode()
        result = 31 * result + shouldDismissOnBackPress.hashCode()
        result = 31 * result + isAppearanceLightStatusBars.hashCode()
        result = 31 * result + isAppearanceLightNavigationBars.hashCode()
        return result
    }
}


/**
 * <a href="https://m3.material.io/components/bottom-sheets/overview" class="external"
 * target="_blank">Material Design modal bottom sheet</a>.
 *
 * Modal bottom sheets are used as an alternative to inline menus or simple dialogs on mobile,
 * especially when offering a long list of action items, or when items require longer descriptions
 * and icons. Like dialogs, modal bottom sheets appear in front of app content, disabling all other
 * app functionality when they appear, and remaining on screen until confirmed, dismissed, or a
 * required action has been taken.
 *
 * ![Bottom sheet
 * image](https://developer.android.com/images/reference/androidx/compose/material3/bottom_sheet.png)
 *
 * A simple example of a modal bottom sheet looks like this:
 *
 * @sample androidx.compose.material3.samples.ModalBottomSheetSample
 * @param onDismissRequest Executes when the user clicks outside of the bottom sheet, after sheet
 *   animates to [Hidden].
 * @param modifier Optional [Modifier] for the bottom sheet.
 * @param sheetState The state of the bottom sheet.
 * @param sheetMaxWidth [Dp] that defines what the maximum width the sheet will take. Pass in
 *   [Dp.Unspecified] for a sheet that spans the entire screen width.
 * @param shape The shape of the bottom sheet.
 * @param containerColor The color used for the background of this bottom sheet
 * @param contentColor The preferred color for content inside this bottom sheet. Defaults to either
 *   the matching content color for [containerColor], or to the current [LocalContentColor] if
 *   [containerColor] is not a color from the theme.
 * @param tonalElevation when [containerColor] is [ColorScheme.surface], a translucent primary color
 *   overlay is applied on top of the container. A higher tonal elevation value will result in a
 *   darker color in light theme and lighter color in dark theme. See also: [Surface].
 * @param scrimColor Color of the scrim that obscures content when the bottom sheet is open.
 * @param dragHandle Optional visual marker to swipe the bottom sheet.
 * @param windowInsets window insets to be passed to the bottom sheet content via [PaddingValues]
 *   params.
 * @param properties [ModalBottomSheetProperties] for further customization of this modal bottom
 *   sheet's window behavior.
 * @param content The content to be displayed inside the bottom sheet.
 */



// Fork of androidx.compose.ui.window.DialogLayout
// Additional parameters required for current predictive back implementation.
@Suppress("ViewConstructor")
internal class ModalBottomSheetDialogLayout(
    context: Context,
    override val window: Window,
    val shouldDismissOnBackPress: Boolean,
    private val onDismissRequest: () -> Unit,
    private val predictiveBackProgress: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope,
) : AbstractComposeView(context), DialogWindowProvider {

    private var content: @Composable () -> Unit by mutableStateOf({})

    private var backCallback: Any? = null

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    fun setContent(parent: CompositionContext, content: @Composable () -> Unit) {
        setParentCompositionContext(parent)
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
        createComposition()
    }

    // Display width and height logic removed, size will always span fillMaxSize().

    @Composable
    override fun Content() {
        content()
    }

    // Existing predictive back behavior below.
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        maybeRegisterBackCallback()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        maybeUnregisterBackCallback()
    }

    private fun maybeRegisterBackCallback() {
        if (!shouldDismissOnBackPress || Build.VERSION.SDK_INT < 33) {
            return
        }
        if (backCallback == null) {
            backCallback =
                if (Build.VERSION.SDK_INT >= 34) {
                    Api34Impl.createBackCallback(onDismissRequest, predictiveBackProgress, scope)
                } else {
                    Api33Impl.createBackCallback(onDismissRequest)
                }
        }
        Api33Impl.maybeRegisterBackCallback(this, backCallback)
    }

    private fun maybeUnregisterBackCallback() {
        if (Build.VERSION.SDK_INT >= 33) {
            Api33Impl.maybeUnregisterBackCallback(this, backCallback)
        }
        backCallback = null
    }

    @RequiresApi(34)
    private object Api34Impl {
        @JvmStatic
        fun createBackCallback(
            onDismissRequest: () -> Unit,
            predictiveBackProgress: Animatable<Float, AnimationVector1D>,
            scope: CoroutineScope
        ) =
            object : OnBackAnimationCallback {
                override fun onBackStarted(backEvent: BackEvent) {
                    scope.launch {
                        predictiveBackProgress.snapTo(PredictiveBack.transform(backEvent.progress))
                    }
                }

                override fun onBackProgressed(backEvent: BackEvent) {
                    scope.launch {
                        predictiveBackProgress.snapTo(PredictiveBack.transform(backEvent.progress))
                    }
                }

                override fun onBackInvoked() {
                    onDismissRequest()
                }

                override fun onBackCancelled() {
                    scope.launch { predictiveBackProgress.animateTo(0f) }
                }
            }
    }

    @RequiresApi(33)
    private object Api33Impl {
        @JvmStatic
        fun createBackCallback(onDismissRequest: () -> Unit) =
            OnBackInvokedCallback(onDismissRequest)

        @JvmStatic
        fun maybeRegisterBackCallback(view: View, backCallback: Any?) {
            if (backCallback is OnBackInvokedCallback) {
                view
                    .findOnBackInvokedDispatcher()
                    ?.registerOnBackInvokedCallback(
                        OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                        backCallback
                    )
            }
        }

        @JvmStatic
        fun maybeUnregisterBackCallback(view: View, backCallback: Any?) {
            if (backCallback is OnBackInvokedCallback) {
                view.findOnBackInvokedDispatcher()?.unregisterOnBackInvokedCallback(backCallback)
            }
        }
    }
}

