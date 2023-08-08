package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun ExtendedFabIconRight(
    @StringRes text: Int,
    icon: ImageVector,
    @StringRes contentDescription: Int,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        modifier = Modifier
            .zIndex(200f)
            .padding(all = 10.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .sizeIn(minWidth = 80.dp)
                .padding(start = 16.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Row(Modifier.clearAndSetSemantics {}) {
                Text(text = stringResource(id = text))
                Spacer(Modifier.width(12.dp))
            }

            Icon(
                imageVector = icon,
                contentDescription = stringResource(id = contentDescription),
            )
        }
    }
}

@Composable
fun ExtendedFabIconLeft(
    @StringRes text: Int,
    icon: ImageVector,
    @StringRes contentDescription: Int,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        modifier = Modifier
            .zIndex(200f)
            .padding(all = 10.dp),
        containerColor = Color.Transparent,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .sizeIn(minWidth = 80.dp)
                .padding(start = 16.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(id = contentDescription),
            )

            Row(Modifier.clearAndSetSemantics {}) {
                Text(text = stringResource(id = text))
                Spacer(Modifier.width(12.dp))
            }
        }
    }
}