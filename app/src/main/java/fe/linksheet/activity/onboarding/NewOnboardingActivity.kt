package fe.linksheet.activity.onboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.extension.android.initPadding
import fe.linksheet.extension.compose.setContentWithKoin
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.AppHost
import fe.linksheet.ui.PoppinsFontFamily
import fe.linksheet.ui.Typography
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewOnboardingActivity : ComponentActivity() {
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

        setContentWithKoin {
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
                    Unit
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

//                if (onboardingScreen is ActionOnboardingScreen<*>) {
//                    composeState =
//                        (onboardingScreen as ActionOnboardingScreen<*>).composeSetup(
//                            back,
//                            next
//                        )
//                }

                Scaffold(
                    modifier = Modifier,
//                        .fillMaxSize(),
//                        .angledGradientBackground(
//                            if (isLightTheme) listOf(
//                                Color(0xFFF8F8F8),
//                                Color(0xFFECEBE9)
//                            ) else listOf(Color(0xFF220C0C), Color(0xFF382D2D)),
//                            -135f
//                        ),
//                    containerColor = Color.Transparent,
//                    contentColor = Color.Transparent,
                    topBar = {
                        fe.linksheet.extension.compose.LargeTopAppBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            containerColor = Color.Transparent,
                            title = {
                                if (pagerState.currentPage == 0) {
                                    Text(
                                        text = stringResource(
                                            id = R.string.welcome_to,
                                            stringResource(id = R.string.app_name)
                                        ),
                                        overflow = TextOverflow.Visible,
                                        style = Typography.titleLarge.copy(
                                            fontFamily = PoppinsFontFamily,
                                            fontSize = 32.sp
                                        ),
                                    )
                                } else if (pagerState.currentPage == 1) {
                                    Text(
                                        text = stringResource(id = R.string.onboarding_1_title),
                                        overflow = TextOverflow.Visible,
                                        style = Typography.titleLarge.copy(
                                            fontFamily = PoppinsFontFamily,
                                            fontSize = 32.sp
                                        ),
                                    )
                                } else if (pagerState.currentPage == 2) {
                                    Text(
                                        text = stringResource(id = R.string.onboarding_2_title),
                                        overflow = TextOverflow.Visible,
                                        style = Typography.titleLarge.copy(
                                            fontFamily = PoppinsFontFamily,
                                            fontSize = 32.sp
                                        ),
                                    )
                                }else if (pagerState.currentPage == 3) {
                                    Text(
                                        text = stringResource(id = R.string.onboarding_3_title),
                                        overflow = TextOverflow.Visible,
                                        style = Typography.titleLarge.copy(
                                            fontFamily = PoppinsFontFamily,
                                            fontSize = 32.sp
                                        ),
                                    )
                                }
                            },
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
//                            scrollBehavior = scrollBehavior
                        )
                    },
//                    floatingActionButton = {},
//                    floatingActionButtonPosition = FabPosition.End,
                ) { padding ->
                    HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState) { page ->
                        if (page == 0) {
                            Screen0(padding = padding, onNextClick = next)
                        } else if (page == 1) {
                            Screen1(padding = padding, onNextClick = next)
                        } else if (page == 2) {
                            Screen2(padding = padding, onNextClick = next)
                        } else if (page == 3) {
                            Screen3(padding = padding, onNextClick = next)
                        }
                    }
                }
            }
        }
    }
}
