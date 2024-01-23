package fe.linksheet.activity.onboarding

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import fe.linksheet.R
import fe.linksheet.extension.android.initPadding
import fe.linksheet.extension.compose.angledGradientBackground
import fe.linksheet.extension.compose.setContentWithKoin
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.AppHost
import fe.linksheet.ui.HkGroteskFontFamily
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
                    modifier = Modifier
                        .fillMaxSize()
                        .angledGradientBackground(
                            if (isLightTheme) listOf(
                                Color(0xFFF8F8F8),
                                Color(0xFFECEBE9)
                            ) else listOf(Color(0xFF220C0C), Color(0xFF382D2D)),
                            -135f
                        ),
                    containerColor = Color.Transparent,
                    contentColor = Color.Transparent,
                    topBar = {
                        fe.linksheet.extension.compose.LargeTopAppBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            containerColor = Color.Transparent,
                            title = {
                                Text(
                                    text = stringResource(
                                        id = R.string.welcome_to,
                                        stringResource(id = R.string.app_name)
                                    ),
                                    overflow = TextOverflow.Visible,
                                    style = Typography.titleLarge.copy(fontFamily = PoppinsFontFamily),
                                )
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
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxHeight()
                            .zIndex(1f),
                        contentPadding = PaddingValues(horizontal = 30.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(id = R.string.onboarding_subtitle),
                                overflow = TextOverflow.Visible,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontFamily = HkGroteskFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }


                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
//                                .height(700.dp)
                                .zIndex(-1f)
//                                .border(1.dp, Color.Red)
//                                .height(500.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().height(500.dp), horizontalArrangement = Arrangement.Center) {
                                AsyncImage(
//                                    modifier = Modifier.border(2.dp, Color.Blue),
                                    model = R.drawable.onboarding0_notext,
                                    alignment = Alignment.Center,
                                    contentDescription = null,
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .navigationBarsPadding()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
//                                elevation = CardDefaults.cardElevation(defaultElevation = ),
//                                    .wrapContentHeight()
//                                    .wrapContentHeight(),
//                                    .height(100.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(
                                    topStart = 25.dp,
                                    bottomStart = 0.dp,
                                    topEnd = 25.dp,
                                    bottomEnd = 0.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
//                                        .wrapContentHeight()
                                        .padding(all = 25.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.onboarding_setup),
                                        overflow = TextOverflow.Visible,
//                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 20.sp,
                                        fontFamily = HkGroteskFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(text = stringResource(id = R.string.start_setup_explainer))

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        modifier = Modifier
                                            .height(50.dp),
//                                            .fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        onClick = {

                                        }
                                    ) {
                                        Text(text = stringResource(id = R.string.start_setup))
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
