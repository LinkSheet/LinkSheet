package fe.linksheet.activity.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.activity.BaseComponentActivity
import fe.linksheet.activity.main.MainActivity
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.composable.ui.NewTypography
import fe.linksheet.composable.ui.PoppinsFontFamily
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingActivity : BaseComponentActivity() {
    private val onboardingViewModel by viewModel<MainViewModel>()

    private val appBarTitles = arrayOf(
        R.string.onboarding_0_title,
        R.string.onboarding_1_title,
        R.string.onboarding_2_title,
        R.string.onboarding_3_title,
        -1,
        R.string.onboarding_5_title
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            AppTheme {
                val pagerState = rememberPagerState(pageCount = { appBarTitles.size })
                val scope = rememberCoroutineScope()

                val back = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    Unit
                }

                val next = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    Unit
                }

                val main = Intent(this, MainActivity::class.java)

                if (pagerState.currentPage == 4) {
                    Screen4(
                        onBackClick = { scope.launch { pagerState.scrollToPage(3) } },
                        onNextClick = { scope.launch { pagerState.scrollToPage(5) } },
                    )
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
                                    style = NewTypography.titleLarge.copy(
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
