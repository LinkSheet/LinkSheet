package app.linksheet.feature.backup.impl.usecase

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.api.RestoreMode
import app.linksheet.feature.backup.impl.core.BackupConfiguration
import app.linksheet.feature.backup.impl.core.PreferenceRepositoryBackup
import app.linksheet.feature.backup.impl.core.PreferenceType
import de.infix.testBalloon.framework.core.TestConfig
import de.infix.testBalloon.framework.core.testSuite
import de.infix.testBalloon.integration.robolectric.ApplicationLifetime
import de.infix.testBalloon.integration.robolectric.RobolectricTestSuiteContent
import de.infix.testBalloon.integration.robolectric.robolectric
import de.infix.testBalloon.integration.robolectric.robolectricTestSuite
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.PreferenceRepository
import okio.Buffer

val RestoreUseCaseTestSuite by testSuite {
    for (apiLevel in listOf(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)) {
        robolectricTestSuite<RestoreUseCaseTestSuiteImpl>(
            "API level $apiLevel",
            testConfig = TestConfig.robolectric {
                sdk = apiLevel
                qualifiers = "xlarge-port"
                applicationLifetime = ApplicationLifetime.RobolectricTestSuite
            }
        )
    }
}
class TestCase(
    val input: String,

)


internal class RestoreUseCaseTestSuiteImpl : RobolectricTestSuiteContent({
    val fixture = testFixture {
        PreferenceRepositoryBackup(
            type = PreferenceType.Preferences,
            repository = object : PreferenceRepository(robolectricApplicationContext) {},
            definition = object : PreferenceDefinition() {
                val PREFERENCE_VALUE_SET_NON_DEFAULT = boolean("PREFERENCE_VALUE_SET_NON_DEFAULT", false)
                val PREFERENCE_VALUE_SET_DEFAULT = boolean("PREFERENCE_VALUE_SET_DEFAULT", false)
                val PREFERENCE_DEFAULT_NEVER_CHANGED = int("PREFERENCE_DEFAULT_NEVER_CHANGED", 15)

                init {
                    finalize()
                }
            },
        )
    }
    fixture asParameterForEach {
        test("merge mode") { holder ->
            val input = """
            {
              "preferences": [
                {
                  "name": "PREFERENCE_VALUE_SET_NON_DEFAULT",
                  "value": "false"
                }
              ]
            }"""
            val configuration = BackupConfiguration(
                preferenceBackups = listOf(holder),
            )
            val useCase = RestoreUseCase(configuration)
            holder.repository.put(holder.definition.all["PREFERENCE_VALUE_SET_NON_DEFAULT"] as Preference.Boolean, true)
            val buffer = Buffer().writeUtf8(input)
            useCase.import(buffer, ImportSettings(RestoreMode.Merge))
        }
    }
})

internal val robolectricApplicationContext: Context
    get() = ApplicationProvider.getApplicationContext()
