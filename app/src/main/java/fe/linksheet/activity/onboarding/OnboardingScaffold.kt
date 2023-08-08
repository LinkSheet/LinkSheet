package fe.linksheet.activity.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import fe.linksheet.ui.PoppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScaffold(
    modifier: Modifier = Modifier,
    headline: String?,
    highlighted: String?,
    @DrawableRes drawable: Int,
    textAlign: TextAlign = TextAlign.Start,
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(drawable),
                contentScale = ContentScale.Crop
            ),
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
        topBar = {
            LargeTopAppBar(
                title = {
                    if (headline != null && highlighted != null) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xFF2B7AE2))) {
                                    append(headline)
                                }

                                append("\n")

                                withStyle(style = SpanStyle(color = Color(0xFF150E56))) {
                                    append(highlighted)
                                }
                            },
                            overflow = TextOverflow.Visible,
                            fontSize = 30.sp,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = textAlign
                        )
                    }
                },
                containerColor = Color.Transparent,
                titleHorizontalArrangement = if (textAlign == TextAlign.Start) Arrangement.Start else Arrangement.End,
                navigationIcon = {},
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        content = { padding -> content(padding) }
    )
}

