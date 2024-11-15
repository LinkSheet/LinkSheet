/*
 * Copyright 2024 The Android Open Source Project
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

package fe.androidx.compose.material3

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import fe.androidx.compose.material3.tokens.MotionSchemeKeyTokens

/**
 * Helper function for component motion tokens.
 *
 * Here is an example on how to use component motion tokens:
 * ``MaterialTheme.motionScheme.fromToken(ExtendedFabBranded.ExpandMotion)``
 *
 * The returned [FiniteAnimationSpec] is remembered across compositions.
 *
 * @param value the token's value
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Stable
internal fun <T> androidx.compose.material3.MotionScheme.fromToken(value: MotionSchemeKeyTokens): FiniteAnimationSpec<T> {
    return when (value) {
        MotionSchemeKeyTokens.DefaultSpatial -> defaultSpatialSpec()
        MotionSchemeKeyTokens.FastSpatial -> fastSpatialSpec()
        MotionSchemeKeyTokens.SlowSpatial -> slowSpatialSpec()
        MotionSchemeKeyTokens.DefaultEffects -> defaultEffectsSpec()
        MotionSchemeKeyTokens.FastEffects -> fastEffectsSpec()
        MotionSchemeKeyTokens.SlowEffects -> slowEffectsSpec()
    }
}

/**
 * Converts a [MotionSchemeKeyTokens] key to the [FiniteAnimationSpec] provided by the
 * [MotionScheme].
 */
@Composable
@ReadOnlyComposable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal fun <T> MotionSchemeKeyTokens.value(): FiniteAnimationSpec<T> =
    MaterialTheme.motionScheme.fromToken(this)
