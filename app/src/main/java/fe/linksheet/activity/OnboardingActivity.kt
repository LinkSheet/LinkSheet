package fe.linksheet.activity

import android.app.role.RoleManager
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import fe.linksheet.R
import fe.linksheet.ui.AppHost
import fe.linksheet.util.AndroidVersion

class OnboardingActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val roleManager by lazy {
            if (AndroidVersion.AT_LEAST_API_26_O) {
                getSystemService<RoleManager>()
            } else null
        }



        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        window.setBackgroundDrawable(ColorDrawable(0))
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val type = if (AndroidVersion.AT_LEAST_API_26_O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

        window.setType(type)

        setContent {
            val navController = rememberAnimatedNavController()

            val browserLauncherAndroidQPlus = if (AndroidVersion.AT_LEAST_API_29_Q) {
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
//                    defaultBrowserChanged(
//                        if (it.resultCode == RESULT_OK) Results.success()
//                        else Results.error()
//                    )
                    }
                )
            } else null

            AppHost {
                Onboarding()
            }
        }
    }
}


val onboardingScreens = listOf(
    R.drawable.onboarding0,
    R.drawable.onboarding1,
    R.drawable.onboarding2,
    R.drawable.onboarding3
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Onboarding() {
    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { onboardingScreens.size })
    val coroutineScope = rememberCoroutineScope()

    Box {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            Image(
                painter = painterResource(onboardingScreens[page]),
                contentDescription = "image",
                modifier = Modifier.align(Alignment.Center),
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .zIndex(2f)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray
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
//                    Brush.verticalGradient(
//                        colors = listOf(Color.Transparent, Color.Black),
//                        startY = 0f,
//                        endY = 2000f
//                    )
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
                    .navigationBarsPadding()
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .zIndex(200f)
                        .padding(all = 10.dp),
                    onClick = {
                        if (pagerState.currentPage + 1 <= pagerState.pageCount) {
                            context.startActivity(Intent(context, MainActivity::class.java))
//                            (context as Activity).overridePendingTransition(
//                                R.anim.fade_enter,
//                                R.anim.fade_enter
//                            )
//                            coroutineScope.launch {
//                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
//                            }
                        }
                    }
                ) {
                    val startPadding = 16.dp
                    val endPadding = 20.dp

                    Row(
                        modifier = Modifier
                            .sizeIn(minWidth = 80.dp)
                            .padding(start = startPadding, end = endPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Row(Modifier.clearAndSetSemantics {}) {
                            Text(text = stringResource(id = R.string.get_started))
                            Spacer(Modifier.width(12.dp))
                        }

                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = stringResource(id = R.string.next),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Onboarding0Preview() {
    Onboarding()
}
