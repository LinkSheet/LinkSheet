package fe.linksheet.activity.onboarding2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import fe.linksheet.activity.onboarding.LargeTopAppBar
import fe.linksheet.activity.onboarding.Onboarding0Screen
import fe.linksheet.activity.onboarding.Onboarding1Screen
import fe.linksheet.activity.onboarding.Onboarding2Screen
import fe.linksheet.activity.onboarding.Onboarding3Screen
import fe.linksheet.activity.onboarding.Onboarding4Screen
import fe.linksheet.activity.onboarding.Onboarding5Screen
import fe.linksheet.activity.onboarding.Onboarding6Screen
import fe.linksheet.activity.onboarding.Onboarding7Screen
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.AppHost
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingActivity2 : ComponentActivity() {
    private val onboardingViewModel by viewModel<MainViewModel>()


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


    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        initPadding()

        setContent {
            AppHost {
                val pagerState =
                    rememberPagerState(pageCount = { fe.linksheet.activity.onboarding.onboardingScreens.size })
                var counter by remember { mutableIntStateOf(0) }

                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState(),
                    canScroll = { true }
                )

//                val

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
//            .angledGradientBackground(listOf(Color(0xFFF8F8F8), Color(0xFFECEBE9)), -135.0f)
//            .paint(
//                painter = painterResource(drawable),
//                contentScale = ContentScale.Crop
//            )
//                    containerColor = Color.Transparent,
//                    contentColor = Color.Transparent,
                    topBar = {
                        LargeTopAppBar(
                            title = {
                                    Text(text = "Hallo $counter")
//                                if (headline != null && highlighted != null) {
//                                    Text(
//                                        text = buildAnnotatedString {
//                                            withStyle(style = SpanStyle(color = Color(0xFF2B7AE2))) {
//                                                append(headline)
//                                            }
//
//                                            append("\n")
//
//                                            withStyle(style = SpanStyle(color = Color(0xFF150E56))) {
//                                                append(highlighted)
//                                            }
//                                        },
//                                        overflow = TextOverflow.Visible,
//                                        fontSize = 30.sp,
//                                        fontFamily = PoppinsFontFamily,
//                                        fontWeight = FontWeight.SemiBold,
//                                        textAlign = textAlign
//                                    )
//                                }
                            },
//                            containerColor = Color.Transparent,
//                            titleHorizontalArrangement = if (textAlign == TextAlign.Start) Arrangement.Start else Arrangement.End,
                            navigationIcon = {
//                                if (drawBackButton) {
//                                    IconButton(onClick = onBackPressed) {
//                                        Icon(
//                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                            contentDescription = stringResource(R.string.back),
//                                        )
//                                    }
//                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    },
                    floatingActionButton = {},
                    floatingActionButtonPosition = FabPosition.End,
                ) { _ ->
                    Button(onClick = {
                        counter++
                        Log.d("Counter", "$counter")
                    }) {
                        Text("Hi")
                    }
                }


//                OnboardingScaffold(
//                    headline = "Test $counter",
//                    highlighted = "Highlight",
//                    drawBackButton = false,
//                    onBackPressed = {},
//                    drawable = R.drawable.onboarding6_notext,
//                ) {
////                    HorizontalPager(
////                        state = pagerState,
////                        userScrollEnabled = false
////                    ) { page ->
//
//
//
//
////                    }
//                }
            }
        }
    }
}