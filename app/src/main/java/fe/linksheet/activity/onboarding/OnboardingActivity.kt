package fe.linksheet.activity.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.extension.compose.LargeTopAppBar
import fe.linksheet.composable.util.ExtendedFabIconRight
import fe.linksheet.extension.android.initPadding
import fe.linksheet.extension.compose.angledGradientBackground
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.AppHost
import fe.linksheet.ui.PoppinsFontFamily
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingActivity : ComponentActivity() {
    private val onboardingViewModel by viewModel<MainViewModel>()


    private val onboardingScreens = listOf(
        Onboarding0Screen,
        Onboarding1Screen,
        Onboarding2Screen,
        Onboarding3Screen,
        Onboarding4Screen,
        Onboarding5Screen,
        Onboarding6Screen,
        Onboarding7Screen
    )

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPadding()

        setContent {
            AppHost {
                val isLightTheme = onboardingViewModel.theme.value.IsLightTheme()

                val pagerState = rememberPagerState(pageCount = {
                    onboardingScreens.size
                })

                val onboardingScreen by remember(pagerState) {
                    derivedStateOf { onboardingScreens[pagerState.currentPage] }
                }

                val coroutineScope = rememberCoroutineScope()

                val back = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }

                val next = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }

                var composeState by remember { mutableStateOf<Any?>(null) }

                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                    canScroll = { true }
                )

                val headline by remember(pagerState) {
                    derivedStateOf { onboardingScreen.headline }
                }


                val highlighted by remember(pagerState) {
                    derivedStateOf { onboardingScreen.highlight }
                }

                if (onboardingScreen is ActionOnboardingScreen<*>) {
                    composeState =
                        (onboardingScreen as ActionOnboardingScreen<*>).composeSetup(
                            back,
                            next
                        )
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .angledGradientBackground(
//                            listOf(Color.Red, Color.Blue),
                            if (isLightTheme) listOf(
                                Color(0xFFF8F8F8),
                                Color(0xFFECEBE9)
                            ) else listOf(Color(0xFF220C0C), Color(0xFF382D2D)),
                            -135f
                        ),
//                        .angledGradientBackground(
//                            listOf(Color(0xFFF8F8F8), Color(0xFFECEBE9)),
//                            -135.0f
//                        )
                    containerColor = Color.Transparent,
                    contentColor = Color.Transparent,
                    topBar = {
                        LargeTopAppBar(
                            modifier = Modifier,
                            containerColor = Color.Transparent,
                            title = {
                                if (headline != null && highlighted != null) {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(color = if(isLightTheme) Color(0xFF2B7AE2) else Color(
                                                0xFF6A9EE2
                                            )
                                            )) {
                                                append(stringResource(id = headline!!))
                                            }

                                            append("\n")

                                            withStyle(style = SpanStyle(color =  if(isLightTheme) Color(0xFF150E56) else Color(
                                                0xFF483BC2
                                            )
                                            )) {
                                                append(stringResource(id = highlighted!!))
                                            }
                                        },
                                        overflow = TextOverflow.Visible,
                                        fontSize = 30.sp,
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.SemiBold,
//                                        textAlign = textAlign
                                    )
                                }
                            },
//                            containerColor = Color.Transparent,
//                            titleHorizontalArrangement = if (textAlign == TextAlign.Start) Arrangement.Start else Arrangement.End,
                            navigationIcon = {
                                if (pagerState.currentPage > 0) {
                                    IconButton(onClick = { back() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = stringResource(R.string.back),
                                        )
                                    }
                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    },
                    floatingActionButton = {},
                    floatingActionButtonPosition = FabPosition.End,
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (onboardingScreen.lightBackgroundImage == null) Modifier
                                else Modifier.paint(
                                    painter = painterResource(if(isLightTheme) onboardingScreen.lightBackgroundImage!! else onboardingScreen.darkBackgroundImage!!),
                                    contentScale = ContentScale.Crop
                                )
                            )

                    ) {
                        HorizontalPager(
                            state = pagerState,
                            userScrollEnabled = false
                        ) { _ ->
                            if (onboardingScreen is RawOnboardingScreen) {
                                (onboardingScreen as RawOnboardingScreen).Render(back, next)
                            } else if (onboardingScreen is ActionOnboardingScreen<*>) {
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(padding)
                                        .fillMaxHeight(),
                                    contentPadding = PaddingValues(horizontal = 15.dp)
                                ) {
                                    (onboardingScreen as? ActionOnboardingScreen<*>)?.content(
                                        this,
                                        isLightTheme
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .navigationBarsPadding()
                        ) {
                            if (onboardingScreen !is RawOnboardingScreen) {
                                Row(
                                    modifier = Modifier
                                        .height(15.dp)
                                        .fillMaxWidth()
                                        .zIndex(2f)
                                        .align(Alignment.BottomCenter),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(pagerState.pageCount) { iteration ->
                                        val color =
                                            if (pagerState.currentPage == iteration) Color.DarkGray
                                            else MaterialTheme.colorScheme.primaryContainer

                                        Box(
                                            modifier = Modifier
                                                .padding(2.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .size(5.dp)
                                        )
                                    }
                                }


                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .zIndex(1f)
                                        .background(
                                            Brush.verticalGradient(
                                                0F to Color.Black.copy(alpha = 0.05F),
                                                .25F to Color.Black.copy(alpha = 0.2F),
                                                .5F to Color.Black.copy(alpha = 0.3F),
                                                1F to Color.Black.copy(alpha = 0.6F)
                                            )
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(top = 10.dp, bottom = 10.dp)
                                    ) {
                                        ExtendedFabIconRight(
                                            text = onboardingScreen.nextButton,
                                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = R.string.next,
                                            onClick = {
                                                if (pagerState.currentPage == onboardingScreens.size - 1) {
                                                    onboardingViewModel.firstRun.updateState(false)
                                                    startActivity(
                                                        Intent(
                                                            this@OnboardingActivity,
                                                            MainActivity::class.java
                                                        )
                                                    )
                                                } else {
                                                    if (onboardingScreen is ActionOnboardingScreen<*>) (onboardingScreen as ActionOnboardingScreen<*>).fabTapped(
                                                        this@OnboardingActivity,
                                                        composeState,
                                                        back,
                                                        next
                                                    )
                                                    else next()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}