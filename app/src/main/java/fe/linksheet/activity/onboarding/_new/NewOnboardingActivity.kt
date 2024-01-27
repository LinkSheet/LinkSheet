package fe.linksheet.activity.onboarding._new

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.activity.onboarding.*
import fe.linksheet.composable.settings.apps.link.AppsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.util.ExtendedFabIconRight
import fe.linksheet.extension.android.initPadding
import fe.linksheet.extension.compose.setContentWithKoin
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.AppHost
import fe.linksheet.ui.PoppinsFontFamily
import fe.linksheet.ui.Typography
import fe.linksheet.util.AndroidVersion
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewOnboardingActivity : ComponentActivity() {
    private val onboardingViewModel by viewModel<MainViewModel>()

    private val appBarTitles = arrayOf(
        R.string.onboarding_0_title,
        R.string.onboarding_1_title,
        R.string.onboarding_2_title,
        R.string.onboarding_3_title,
        -1,
        R.string.onboarding_5_title
    )

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPadding()

        setContentWithKoin {
            AppHost {
                val isLightTheme = onboardingViewModel.theme.value.IsLightTheme()

                val pagerState = rememberPagerState(pageCount = { appBarTitles.size })

                val coroutineScope = rememberCoroutineScope()

                val back = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    Unit
                }

                val next = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    Unit
                }

                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                    canScroll = { true }
                )

                val main = Intent(this, MainActivity::class.java)



                if (pagerState.currentPage == 4) {
                    Screen4(
                        onBackPressed = { coroutineScope.launch { pagerState.scrollToPage(3) } },
                        onNextClick = { coroutineScope.launch { pagerState.scrollToPage(5) } },
                    )
//                    if (AndroidVersion.AT_LEAST_API_31_S) {
//                        Column {
//                            AppsWhichCanOpenLinksSettingsRoute(onBackPressed = {
//
//                            })
//
//                            Column(
//                                modifier = Modifier
////                                    .align(Alignment.BottomStart)
//                                    .padding(vertical = 10.dp, horizontal = 25.dp)
//                                    .navigationBarsPadding()
//                            ) {
//                                Button(
//                                    modifier = Modifier.height(50.dp),
//                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
//                                    onClick = next
//                                ) {
//                                    Text(text = stringResource(id = R.string.onboarding_4_button))
//                                }
//                            }
//                        }
//                    }
                } else {
                    Scaffold(topBar = {
                        fe.linksheet.extension.compose.LargeTopAppBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            containerColor = Color.Transparent,
                            title = {
                                Text(
                                    text = stringResource(id = appBarTitles[pagerState.currentPage]),
                                    overflow = TextOverflow.Visible,
                                    style = Typography.titleLarge.copy(
                                        fontFamily = PoppinsFontFamily,
                                        fontSize = 32.sp
                                    ),
                                )
                            },
                            navigationIcon = {
                                if (pagerState.currentPage > 0) {
                                    IconButton(onClick = back) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = stringResource(R.string.back),
                                        )
                                    }
                                }
                            },
                        )
                    }) { padding ->
                        HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState) { page ->
                            when (page) {
                                0 -> Screen0(padding = padding, onNextClick = next)
                                1 -> Screen1(padding = padding, onNextClick = next)
                                2 -> Screen2(padding = padding, onNextClick = next)
                                3 -> Screen3(padding = padding, onNextClick = next)
                                5 -> Screen5(padding = padding, onNextClick = {
                                    startActivity(main)
                                    finish()
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}
