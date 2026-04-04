package fe.linksheet.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.akuleshov7.ktoml.Toml
import com.google.gson.Gson
import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
class ExportImportUseCaseTest : BaseUnitTest {
    private val repository = DefaultAppPreferenceRepository(applicationContext)

    @org.junit.Test
    fun test() {
        val useCase = ExportImportUseCase(repository, Gson(), Toml.Default)
        println(useCase.export(false))
    }
}
