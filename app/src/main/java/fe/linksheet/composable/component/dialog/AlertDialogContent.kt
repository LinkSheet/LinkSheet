package fe.linksheet.composable.component.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties
import fe.composekit.component.dialog.DialogDefaults


@Composable
fun AlertDialogContent(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) {
    Surface(modifier = modifier, shape = shape, color = containerColor, tonalElevation = tonalElevation,) {
        Column(modifier = Modifier.padding(DialogDefaults.DialogPadding)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Column {
//                    Image(
//                        painter = painterResource(id = R.drawable.app_linksheet),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(48.dp)
//                            .clip(CircleShape),
//                    )
//
//                    Spacer(modifier = Modifier.height(10.dp))
//
//                    Text(
//                        text = stringResource(id = R.string.app_name),
//                        fontFamily = HkGroteskFontFamily,
//                        fontWeight = FontWeight.SemiBold,
//                        fontSize = 18.sp
//                    )
//
//                    Text(text = AppInfo.buildInfo.flavor)
//
//                    Text(text = AppInfo.buildInfo.versionName)
//                    Text(text = AppInfo.buildInfo.builtAt)
                }
            }
        }
    }
}
//    Surface(
//        modifier = modifier,
//        shape = shape,
//        color = containerColor,
//        tonalElevation = tonalElevation,
//    ) {
//        Column(modifier = Modifier.padding(DialogPadding)) {
//            icon?.let {
//                CompositionLocalProvider(LocalContentColor provides iconContentColor) {
//                    Box(
//                        Modifier
//                            .padding(IconPadding)
//                            .align(Alignment.CenterHorizontally)) {
//                        icon()
//                    }
//                }
//            }
//            title?.let {
//                ProvideContentColorTextStyle(
//                    contentColor = titleContentColor,
//                    textStyle = DialogTokens.HeadlineFont.value
//                ) {
//                    Box(
//                        // Align the title to the center when an icon is present.
//                        Modifier
//                            .padding(TitlePadding)
//                            .align(
//                                if (icon == null) {
//                                    Alignment.Start
//                                } else {
//                                    Alignment.CenterHorizontally
//                                }
//                            )
//                    ) {
//                        title()
//                    }
//                }
//            }
//            text?.let {
//                val textStyle = DialogTokens.SupportingTextFont.value
//                ProvideContentColorTextStyle(
//                    contentColor = textContentColor,
//                    textStyle = textStyle
//                ) {
//                    Box(
//                        Modifier
//                            .weight(weight = 1f, fill = false)
//                            .padding(TextPadding)
//                            .align(Alignment.Start)
//                    ) {
//                        text()
//                    }
//                }
//            }
//            Box(modifier = Modifier.align(Alignment.End)) {
//                val textStyle = DialogTokens.ActionLabelTextFont.value
//                ProvideContentColorTextStyle(
//                    contentColor = buttonContentColor,
//                    textStyle = textStyle,
//                    content = buttons
//                )
//            }
//        }
//    }
//}
