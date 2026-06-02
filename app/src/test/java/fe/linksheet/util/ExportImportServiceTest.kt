package fe.linksheet.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.api.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class ExportImportServiceTest : BaseUnitTest {
    private val repository = DefaultAppPreferenceRepository(applicationContext)
//    private val useCase = ExportImportUseCase(repository, Json.Default, Toml.Default)
//    private val viewModel = ExportSettingsViewModel(
//        applicationContext as Application, repository, Gson(), Clock.System,
//        useCase
//    )

    @OptIn(SensitivePreference::class)
    @org.junit.Test
    fun test() {
        val set = AppPreferences.sensitivePreferences
//        val preferences = repository.exportPreferences(set)
//        println(preferences)
//        println(Toml.encodeToString(preferences))
//        viewModel.exportPreferences()
//        pyyyy-MM-dd_HH:mm

    }
}

