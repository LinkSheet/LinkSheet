package fe.linksheet.experiment.ui.overhaul.composable.component.page.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults

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
    val ContentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = VerticalSpacing, bottom = 6.dp)

    val TextDividerPadding = PaddingValues(
        start = 16.dp,
        top = VerticalSpacing,
        bottom = VerticalSpacing
    )

    val GroupSpacingTop = PaddingValues(bottom = 1.dp)
    val GroupSpacingMiddle = PaddingValues(vertical = 1.dp)
    val GroupSpacingBottom = PaddingValues(top = 1.dp)
}

@Composable
fun SaneLazyColumnPageLayout(
    padding: PaddingValues,
    contentPadding: PaddingValues = SaneLazyColumnPageDefaults.ContentPadding,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: SaneLazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        content = { content(SaneLazyListScopeImpl(this)) }
    )
}
