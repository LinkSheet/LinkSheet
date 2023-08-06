package fe.linksheet.activity.onboarding

import android.app.Activity
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
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import fe.linksheet.R
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.Results
import kotlinx.coroutines.Job
import fe.linksheet.activity.onboarding.ActionOnboardingScreen as ActionOnboardingScreen1


abstract class OnboardingScreen(
    @DrawableRes val backgroundImage: Int,
    val textAlign: TextAlign,
    @StringRes val nextButton: Int,
    @StringRes val headline: Int?,
    @StringRes val highlight: Int?,
)

abstract class ImageOnboardingScreen(
    @DrawableRes backgroundImage: Int,
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
) : OnboardingScreen(R.drawable.gradient, textAlign, nextButton, headline, highlight) {
    abstract fun content(scope: LazyListScope)

    @Composable
    abstract fun composeSetup(next: () -> Job): T

    abstract fun fabTapped(context: Context, obj: Any?, next: () -> Job)
}

data object Onboarding0Screen : ImageOnboardingScreen(
    R.drawable.onboarding0_notext,
    TextAlign.Start,
    R.string.get_started,
    R.string.onboarding0_headline,
    R.string.onboarding0_highlight
)

data object Onboarding1Screen :
    ActionOnboardingScreen1<ManagedActivityResultLauncher<Intent, ActivityResult>?>(
        R.string.set_as_default_browser,
        TextAlign.Start,
        R.string.onboarding1_headline,
        R.string.app_name
    ) {
    override fun content(scope: LazyListScope) {
        scope.item(key = "text") {
            Text(
                text = buildAnnotatedString {
                    append(text = stringResource(id = R.string.welcome_to_linksheet_explainer))
                    append(text = "\n")
                    append(text = stringResource(id = R.string.welcome_to_linksheet_explainer_2))
                },
                fontSize = 15.sp,
                color = Color.Black,
            )
        }
    }

    @Composable
    override fun composeSetup(next: () -> Job): ManagedActivityResultLauncher<Intent, ActivityResult>? {
        return if (AndroidVersion.AT_LEAST_API_29_Q) {
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = {
                    next()
                }
            )
        } else null
    }

    override fun fabTapped(context: Context, obj: Any?, next: () -> Job) {
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
    R.drawable.onboarding2_notext,
    TextAlign.End,
    R.string.next,
    R.string.onboarding2_headline,
    R.string.onboarding2_highlight
)

data object Onboarding3Screen : ImageOnboardingScreen(
    R.drawable.onboarding3_notext,
    TextAlign.Start,
    R.string.get_started,
    R.string.onboarding3_headline,
    R.string.onboarding3_highlight
)

data object Onboarding4Screen : ImageOnboardingScreen(
    R.drawable.onboarding4_notext,
    TextAlign.Start,
    R.string.get_started,
    null,
    null
)

data object Onboarding5Screen : ImageOnboardingScreen(
    R.drawable.onboarding5_notext,
    TextAlign.End,
    R.string.get_started,
    R.string.onboarding5_headline,
    R.string.onboarding5_highlight
)
