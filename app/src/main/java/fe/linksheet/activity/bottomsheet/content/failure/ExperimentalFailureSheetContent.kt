package fe.linksheet.activity.bottomsheet.content.failure

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.compose.preview.PreviewContainer
import fe.android.compose.dialog.helper.DialogMaxWidth
import fe.android.compose.dialog.helper.DialogMinWidth
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.ProvideContentColorTextStyle
import fe.composekit.component.dialog.AlertDialogFlowRow
import fe.composekit.route.Route
import fe.linksheet.R
import fe.linksheet.util.intent.parser.*


@Composable
fun FailureSheetContentWrapper(
    modifier: Modifier = Modifier,
    onSearchClick: (String) -> Unit,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    exception: UriException,
) {
    if (exception is NoUriFoundRecoverableException) {
        ExperimentalFailureSheetContent(
            modifier = modifier,
            onSearchClick = onSearchClick,
            onShareClick = onShareClick,
            onCopyClick = onCopyClick,
            data = exception.recoverable,
        )
    }
}

@Composable
fun ExperimentalFailureSheetContent(
    modifier: Modifier = Modifier,
    onSearchClick: (String) -> Unit,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    data: String,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(min = DialogMinWidth, max = DialogMaxWidth)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.secondary) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                    )
                }
            }
            ProvideContentColorTextStyle(
                contentColor = MaterialTheme.colorScheme.onSurface,
                textStyle = MaterialTheme.typography.headlineSmall
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(id = R.string.link_handle_failed),
                    )
                }
            }

            ProvideContentColorTextStyle(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                textStyle = MaterialTheme.typography.bodyMedium
            ) {
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = false)
                        .padding(bottom = 24.dp)
                        .align(Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(text = stringResource(id = R.string.link_handle_failed_text))

                    LinkCard(data = data, navigate = {})
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.End)) {
            AlertDialogFlowRow {
                FilledTonalButton(
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    onClick = { }
                ) {
                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = stringResource(id = R.string.retry)
                    )

                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

                    Text(text = stringResource(id = R.string.retry))
                }

                Button(
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,

                    onClick = {
                        onSearchClick(data)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search)
                    )

                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

                    Text(text = stringResource(id = R.string.search))
                }
            }
        }
    }
}

@Composable
private fun LinkCard(data: String, navigate: (Route) -> Unit) {
    val context = LocalContext.current

    FailureSheetLinkCard(
        onClick = { },
        icon = Icons.Rounded.Public.iconPainter,
        iconContentDescription = stringResource(id = R.string.generic__text_url),
//        headline = textContent(R.string.nightly_experiments_card),
//        subtitle = text(data),
        text = content {
            Text(text = data, overflow = TextOverflow.Clip, maxLines = 3)
        },
//        content = {
//            EditExperiment(uriString = data, navigate = navigate)
//        }
    )
}


@Preview(showBackground = true, widthDp = 400)
@Composable
private fun ExperimentalFailureSheetColumnPreview() {
    PreviewContainer {
//        Surface(
//        modifier = modifier,
//        shape = shape,
//            color = MaterialTheme.colorScheme.surfaceContainerHigh,
//            tonalElevation = 6.0.dp,
//        ) {
        ExperimentalFailureSheetContent(
            onSearchClick = {},
            onShareClick = {},
            onCopyClick = {},
            data = "https://linksheet.app"
        )
//        }
    }
}

@Preview
@Composable
private fun LinkCardPreview() {
    PreviewContainer {
        LinkCard(data = "https://linksheet.app", navigate = {})
    }
}

