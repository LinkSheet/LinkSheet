package fe.linksheet.util

import android.app.Application
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.akuleshov7.ktoml.Toml
import com.google.gson.Gson
import app.linksheet.api.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
import fe.linksheet.module.viewmodel.ExportSettingsViewModel
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.ZoneId
import kotlin.time.Clock


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class ImportExportServiceTest : BaseUnitTest {
    private val service = ImportExportService(applicationContext, Clock.System, ZoneId.systemDefault())
    private val repository = DefaultAppPreferenceRepository(applicationContext)
    private val useCase = ExportImportUseCase(repository, Gson(), Toml.Default)
    private val viewModel = ExportSettingsViewModel(
        applicationContext as Application, repository, Gson(), Clock.System,
        ZoneId.systemDefault(),
        useCase
    )

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

