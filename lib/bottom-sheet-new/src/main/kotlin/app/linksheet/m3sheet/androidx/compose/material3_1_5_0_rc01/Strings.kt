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

package app.linksheet.m3sheet.androidx.compose.material3_1_5_0_rc01

import androidx.annotation.StringRes
import androidx.compose.material3.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

internal object Strings {
    val BottomSheetPaneTitle = R.string.m3c_bottom_sheet_pane_title
    val BottomSheetDragHandleDescription = R.string.m3c_bottom_sheet_drag_handle_description
    val BottomSheetPartialExpandDescription = R.string.m3c_bottom_sheet_collapse_description
    val BottomSheetDismissDescription = R.string.m3c_bottom_sheet_dismiss_description
    val BottomSheetExpandDescription = R.string.m3c_bottom_sheet_expand_description
    val CloseSheet = R.string.m3c_bottom_sheet_dismiss_description
}

@Composable
internal fun getString(@StringRes string: Int): String = stringResource(string)
