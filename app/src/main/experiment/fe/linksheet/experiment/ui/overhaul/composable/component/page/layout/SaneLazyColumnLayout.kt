package fe.linksheet.experiment.ui.overhaul.composable.component.page.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.ShapeListItemDefaults

@Stable
data class GroupItem(
    val contentType: ContentTypeDefaults,
    val padding: PaddingValues = PaddingValues(),
    val shape: Shape,
) {
    companion object {
        val Top = GroupItem(
            ContentTypeDefaults.TopGroupItem,
            SaneLazyColumnPageDefaults.GroupSpacingTop,
            ShapeListItemDefaults.TopShape
        )

        val Middle = GroupItem(
            ContentTypeDefaults.MiddleGroupItem,
            SaneLazyColumnPageDefaults.GroupSpacingMiddle,
            ShapeListItemDefaults.MiddleShape
        )

        val Bottom = GroupItem(
            ContentTypeDefaults.BottomGroupItem,
            SaneLazyColumnPageDefaults.GroupSpacingBottom,
            ShapeListItemDefaults.BottomShape
        )

        val Single = GroupItem(
            ContentTypeDefaults.SingleGroupItem,
            shape = ShapeListItemDefaults.SingleShape
        )
    }
}

object SaneLazyColumnPageDefaults {
    val VerticalSpacing = 12.dp
    val HorizontalSpacing = 20.dp
    val BottomSpacing = 6.dp

    val ContentPadding = PaddingValues(start = HorizontalSpacing, end = HorizontalSpacing, top = VerticalSpacing, bottom = BottomSpacing)

    val TextDividerPadding = PaddingValues(
        start = 16.dp,
        top = VerticalSpacing,
        bottom = VerticalSpacing ,
        end = 16.dp
    )

    val GroupSpacingTop = PaddingValues(bottom = 1.dp)
    val GroupSpacingMiddle = PaddingValues(vertical = 1.dp)
    val GroupSpacingBottom = PaddingValues(top = 1.dp)
}

@Composable
fun SaneLazyColumnPageLayout(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    padding: PaddingValues,
    contentPadding: PaddingValues = SaneLazyColumnPageDefaults.ContentPadding,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: SaneLazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .padding(padding)
            .fillMaxSize(),
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        content = {
            content(SaneLazyListScopeImpl(this))

            item(key = -1) {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    )
}
