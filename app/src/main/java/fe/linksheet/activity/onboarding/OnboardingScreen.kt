package fe.linksheet.activity.onboarding

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import fe.linksheet.R
import fe.linksheet.composable.settings.apps.link.AppsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.util.ExtendedFabIconRight
import fe.linksheet.ui.Theme
import fe.linksheet.util.AndroidVersion
import kotlinx.coroutines.Job


abstract class OnboardingScreen(
    @DrawableRes val backgroundImage: Int?,
    val textAlign: TextAlign,
    @StringRes val nextButton: Int,
    @StringRes val headline: Int?,
    @StringRes val highlight: Int?,
)

abstract class ImageOnboardingScreen(
    @DrawableRes backgroundImage: Int?,
    textAlign: TextAlign,
    @StringRes nextButton: Int,
    @StringRes headline: Int?,
    @StringRes highlight: Int?,
) : OnboardingScreen(
    backgroundImage, textAlign, nextButton, headline, highlight
)

abstract class ActionOnboardingScreen<T>(
    @StringRes nextButton: Int,
    textAlign: TextAlign,
    @StringRes headline: Int,
    @StringRes highlight: Int,
) : OnboardingScreen(null, textAlign, nextButton, headline, highlight) {
    abstract fun content(scope: LazyListScope, lightTheme: Boolean)

    @Composable
    abstract fun composeSetup(back: () -> Job, next: () -> Job): T

    abstract fun fabTapped(context: Context, obj: Any?, back: () -> Job, next: () -> Job)
}

abstract class RawOnboardingScreen(
    @StringRes nextButton: Int,
    textAlign: TextAlign,
    @StringRes headline: Int?,
    @StringRes highlight: Int?,
) : OnboardingScreen(null, textAlign, nextButton, headline, highlight) {
    @Composable
    abstract fun Render(back: () -> Job, next: () -> Job)
}

data object Onboarding0Screen : ImageOnboardingScreen(
    R.drawable.onboarding0_notext_resized,
    TextAlign.Start,
    R.string.get_started,
    R.string.onboarding0_headline,
    R.string.onboarding0_highlight
)

data object Onboarding1Screen :
    ActionOnboardingScreen<ManagedActivityResultLauncher<Intent, ActivityResult>?>(
        R.string.set_as_default_browser,
        TextAlign.Start,
        R.string.onboarding1_headline,
        R.string.app_name
    ) {
    override fun content(scope: LazyListScope, lightTheme: Boolean) {
        scope.item(key = "text") {
            Text(
                text = buildAnnotatedString {
                    append(text = stringResource(id = R.string.welcome_to_linksheet_explainer))
                    append(text = "\n")
                    append(text = stringResource(id = R.string.welcome_to_linksheet_explainer_2))
                },
                fontSize = 15.sp,
                color = if (lightTheme) Color.Black else Color.White,
            )
        }
    }

    @Composable
    override fun composeSetup(
        back: () -> Job,
        next: () -> Job
    ): ManagedActivityResultLauncher<Intent, ActivityResult>? {
        return if (AndroidVersion.AT_LEAST_API_29_Q) {
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = {
                    next()
                }
            )
        } else null
    }

    override fun fabTapped(context: Context, obj: Any?, back: () -> Job, next: () -> Job) {
        if (obj != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService<RoleManager>()
            val casted = (obj as ManagedActivityResultLauncher<Intent, ActivityResult>)

            Log.d("OnboardingScreen", "FabTapped $roleManager $casted")
            kotlin.runCatching {
                obj.launch(roleManager!!.createRequestRoleIntent(RoleManager.ROLE_BROWSER))
            }.onFailure {
                it.printStackTrace()
            }
        } else {
            context.startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        }
    }
}

data object Onboarding2Screen : ImageOnboardingScreen(
    R.drawable.onboarding2_notext_resized,
    TextAlign.End,
    R.string.next,
    R.string.onboarding2_headline,
    R.string.onboarding2_highlight
)

data object Onboarding3Screen : ImageOnboardingScreen(
    R.drawable.onboarding3_notext_resized,
    TextAlign.Start,
    R.string.next,
    R.string.onboarding3_headline,
    R.string.onboarding3_highlight
)

data object Onboarding4Screen : ActionOnboardingScreen<Unit>(
    R.string.disable_link_handling,
    TextAlign.Start,
    R.string.onboarding4_headline,
    R.string.onboarding4_highlight
) {
    override fun content(scope: LazyListScope, lightTheme: Boolean) {
        scope.item(key = "text") {
            Text(
                text = buildAnnotatedString {
                    append(text = stringResource(id = R.string.onboarding4_paragraph_1))
                    append(text = "\n")
                    append(text = stringResource(id = R.string.onboarding4_paragraph_2))
                },
                fontSize = 15.sp,
                color = if (lightTheme) Color.Black else Color.White
            )
        }

        scope.item(key = "padding-bottom") {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    @Composable
    override fun composeSetup(back: () -> Job, next: () -> Job) {

    }

    override fun fabTapped(context: Context, obj: Any?, back: () -> Job, next: () -> Job) {
        next()
    }
}

data object Onboarding5Screen : RawOnboardingScreen(
    R.string.disable_link_handling,
    TextAlign.Start,
    null,
    null
) {
    @Composable
    override fun Render(back: () -> Job, next: () -> Job) {
        Spacer(modifier = Modifier.height(5.dp))

        Box {
            if (AndroidVersion.AT_LEAST_API_31_S) {
                AppsWhichCanOpenLinksSettingsRoute(onBackPressed = { back() })
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(top = 10.dp, bottom = 10.dp)
                    .navigationBarsPadding()
            ) {
                ExtendedFabIconRight(
                    text = R.string.done,
                    icon = Icons.Filled.ArrowForward,
                    contentDescription = R.string.done,
                    onClick = {
                        next()
                    }
                )
            }
        }
    }
}

data object Onboarding6Screen : ImageOnboardingScreen(
    R.drawable.onboarding6_notext_resized,
    TextAlign.End,
    R.string.next,
    R.string.onboarding6_headline,
    R.string.onboarding6_highlight
)

data object Onboarding7Screen : ImageOnboardingScreen(
    R.drawable.onboarding7_notext_resized,
    TextAlign.Start,
    R.string.finish,
    R.string.onboarding7_headline,
    R.string.onboarding7_highlight
)
