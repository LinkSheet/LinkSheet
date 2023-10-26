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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.composable.util.ExtendedFabIconRight
import fe.linksheet.extension.android.initPadding
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.AppHost
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingActivity : ComponentActivity() {
    private val onboardingViewModel by viewModel<MainViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPadding()

        setContent {
            AppHost {
                val context = LocalContext.current

                val pagerState = rememberPagerState(pageCount = { onboardingScreens.size })
                val coroutineScope = rememberCoroutineScope()

                val back = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }

                val next = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }

                val onboardingScreen by remember(pagerState) {
                    derivedStateOf { onboardingScreens[pagerState.currentPage] }
                }

                var composeState by remember { mutableStateOf<Any?>(null) }

                if (onboardingScreen is ActionOnboardingScreen<*>) {
                    composeState =
                        (onboardingScreen as ActionOnboardingScreen<*>).composeSetup(
                            back,
                            next
                        )
                }

                Box {
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = false
                    ) { page ->
                        if (onboardingScreen is RawOnboardingScreen) {
                            (onboardingScreen as RawOnboardingScreen).Render(back, next)
                        } else {
                            OnboardingScaffold(
                                headline = onboardingScreen.headline?.let { stringResource(id = it) },
                                highlighted = onboardingScreen.highlight?.let {
                                    stringResource(
                                        id = it
                                    )
                                },
                                drawable = onboardingScreen.backgroundImage,
                                textAlign = onboardingScreen.textAlign,
                                drawBackButton = pagerState.currentPage > 0,
                                onBackPressed = {
                                    back()
                                }
                            ) { padding ->
                                if (onboardingScreen is ActionOnboardingScreen<*>) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .padding(padding)
                                            .fillMaxHeight(),
                                        contentPadding = PaddingValues(horizontal = 15.dp)
                                    ) {
                                        (onboardingScreen as? ActionOnboardingScreen<*>)?.content(
                                            this
                                        )
                                    }
                                }
                            }
                        }
                    }


                    if (onboardingScreen !is RawOnboardingScreen) {
                        Row(
                            modifier = Modifier
                                .height(30.dp)
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
//                .clip(RoundedCornerShape(18.dp, 18.dp, 0.dp, 0.dp))
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
//                            if(pagerState.currentPage > 0){
//                                Column(
//                                    modifier = Modifier
//                                        .align(Alignment.BottomStart)
//                                        .padding(top = 10.dp, bottom = 10.dp)
//                                        .navigationBarsPadding()
//                                ) {
//                                    ExtendedFabIconLeft(
//                                        text = R.string.back,
//                                        icon = Icons.Filled.ArrowBack,
//                                        contentDescription = R.string.back,
//                                        onClick = {
//                                            back()
//                                        }
//                                    )
//                                }
//                            }


                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(top = 10.dp, bottom = 10.dp)
                                    .navigationBarsPadding()
                            ) {
                                ExtendedFabIconRight(
                                    text = onboardingScreen.nextButton,
                                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = R.string.next,
                                    onClick = {
                                        if (pagerState.currentPage == onboardingScreens.size - 1) {
                                            onboardingViewModel.firstRun.updateState(false)
                                            context.startActivity(
                                                Intent(
                                                    context,
                                                    MainActivity::class.java
                                                )
                                            )
                                        } else {
                                            if (onboardingScreen is ActionOnboardingScreen<*>) (onboardingScreen as ActionOnboardingScreen<*>).fabTapped(
                                                context,
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


val onboardingScreens = listOf(
    Onboarding0Screen,
    Onboarding1Screen,
    Onboarding2Screen,
    Onboarding3Screen,
    Onboarding4Screen,
    Onboarding5Screen,
    Onboarding6Screen,
    Onboarding7Screen
)
