package fe.linksheet.composable.page.home.card

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPasteGo
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.card.AlertCard
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.composekit.route.Route
import fe.linksheet.navigation.TextEditorRoute


@Composable
fun OpenCopiedLink(editClipboard: Boolean, uri: Uri, navigate: (Route) -> Unit) {
    val uriString = uri.toString()
    val context = LocalContext.current

    val editRow = remember(key1 = editClipboard, key2 = uriString) {
        val composable: @Composable () -> Unit = {
            EditExperiment(uriString = uriString, navigate = navigate)
        }

        return@remember if (editClipboard) composable else null
    }

    AlertCard(
        onClick = {
            context.startActivity(Intent(context, BottomSheetActivity::class.java).apply {
                this.action = Intent.ACTION_VIEW
                this.data = uri
            })
        },
        icon = Icons.Outlined.ContentPasteGo.iconPainter,
        iconContentDescription = stringResource(id = R.string.paste),
        headline = textContent(R.string.open_copied_link),
        subtitle = text(uriString),
        content = editRow
    )
}

@Composable
fun EditExperiment(uriString: String, navigate: (Route) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        EditButton(
            id = R.string.generic__text_edit,
            onClick = {
                navigate(TextEditorRoute(uriString))
            }
        )
    }
}

@Composable
fun EditButton(
    @StringRes id: Int,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )

        Spacer(Modifier.size(ButtonDefaults.IconSpacing))

        Text(text = stringResource(id = id))
    }
}

@Preview(apiLevel = 34)
@Composable
private fun OpenCopiedLinkPreview() {
    OpenCopiedLink(editClipboard = true, uri = Uri.parse("https://google.com")) {

    }
}
