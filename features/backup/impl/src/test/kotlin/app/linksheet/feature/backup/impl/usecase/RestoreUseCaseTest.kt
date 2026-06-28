package app.linksheet.feature.backup.impl.usecase

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.backup.api.ImportSettings
import app.linksheet.feature.backup.api.RestoreMode
import app.linksheet.feature.backup.impl.core.BackupConfiguration
import app.linksheet.feature.backup.impl.core.PreferenceRepositoryBackup
import app.linksheet.feature.backup.impl.core.PreferenceType
import assertk.all
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.first
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.result.StdResult
import fe.std.result.assert.assertSuccess
import kotlinx.coroutines.test.runTest
import okio.Buffer
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.BAKLAVA])
internal class RestoreUseCaseTest : BaseUnitTest {

    private suspend fun baseTest(
        useCase: RestoreUseCase,
        input: String,
        settings: ImportSettings
    ): StdResult<RestoreResultWrapper> {
        val buffer = Buffer().writeUtf8(input)
        return useCase.import(buffer, settings)
    }
    private fun testSetup(): Triple<RestoreUseCase, PreferenceRepository, PreferenceDefinition> {
        val repository = object : PreferenceRepository(applicationContext) {}
        val definition = object : PreferenceDefinition() {
            val PREFERENCE_VALUE_SET_NON_DEFAULT = boolean("PREFERENCE_VALUE_SET_NON_DEFAULT", false)
            val PREFERENCE_VALUE_SET_DEFAULT = boolean("PREFERENCE_VALUE_SET_DEFAULT", false)
            val PREFERENCE_DEFAULT_NEVER_CHANGED = int("PREFERENCE_DEFAULT_NEVER_CHANGED", 15)

            init {
                finalize()
            }
        }
        val holder = PreferenceRepositoryBackup(
            type = PreferenceType.Preferences,
            repository = repository,
            definition = definition,
        )
        val configuration = BackupConfiguration(
            preferenceBackups = listOf(holder)
        )
        val useCase = RestoreUseCase(configuration)
        return Triple(useCase, repository, definition)
    }

    private fun PreferenceRepository.getPrefsWithStoredValue(definition: PreferenceDefinition): Map<String, Preference<*, *>> {
        return definition.all.filter { hasStoredValue(it.key) }
    }

    @Test
    fun `test erase restore mode (legacy schema)`() = runTest {
        val input = """
        {
          "preferences": [
            {
              "name": "PREFERENCE_VALUE_SET_NON_DEFAULT",
              "value": "false"
            }
          ]
        }"""
        val (useCase, repository, definition) = testSetup()
        repository.put(definition.all["PREFERENCE_VALUE_SET_NON_DEFAULT"] as Preference.Boolean, true)
        repository.put(definition.all["PREFERENCE_VALUE_SET_DEFAULT"] as Preference.Boolean, false)

        assertThat(repository.getPrefsWithStoredValue(definition).keys).containsExactlyInAnyOrder(
            "PREFERENCE_VALUE_SET_NON_DEFAULT",
            "PREFERENCE_VALUE_SET_DEFAULT"
        )

        val result = baseTest(useCase , input, ImportSettings(RestoreMode.EraseRestore))

        assertSuccess(result).transform { it.entries }.all {
            hasSize(1)
            first().isInstanceOf<PreferenceRestoreEntry>().all {
                prop(PreferenceRestoreEntry::preference).transform { it.key }.isEqualTo("PREFERENCE_VALUE_SET_NON_DEFAULT")
                prop(PreferenceRestoreEntry::hasStoredValue).isEqualTo(false)
                prop(PreferenceRestoreEntry::result).isEqualTo(RestoreResult.Restored)
            }
        }

        assertThat(repository.getPrefsWithStoredValue(definition).keys).containsExactlyInAnyOrder(
            "PREFERENCE_VALUE_SET_NON_DEFAULT",
        )
    }

    @Test
    fun `test replace mode (legacy schema)`() = runTest {
        val input = """
        {
          "preferences": [
            {
              "name": "PREFERENCE_VALUE_SET_NON_DEFAULT",
              "value": "false"
            }
          ]
        }"""
        val (useCase, repository, definition) = testSetup()
        repository.put(definition.all["PREFERENCE_VALUE_SET_NON_DEFAULT"] as Preference.Boolean, true)

        val result = baseTest(useCase , input, ImportSettings(RestoreMode.Replace))

        assertSuccess(result).transform { it.entries }.all {
            hasSize(1)
            first().isInstanceOf<PreferenceRestoreEntry>().all {
                prop(PreferenceRestoreEntry::preference).transform { it.key }.isEqualTo("PREFERENCE_VALUE_SET_NON_DEFAULT")
                prop(PreferenceRestoreEntry::hasStoredValue).isEqualTo(true)
                prop(PreferenceRestoreEntry::result).isEqualTo(RestoreResult.Restored)
            }
        }
    }

    @Test
    fun `test merge mode (legacy schema)`() = runTest {
        val input = """
        {
          "preferences": [
            {
              "name": "PREFERENCE_VALUE_SET_NON_DEFAULT",
              "value": "false"
            }
          ]
        }"""
        val (useCase, repository, definition) = testSetup()
        repository.put(definition.all["PREFERENCE_VALUE_SET_NON_DEFAULT"] as Preference.Boolean, true)

        val result = baseTest(useCase , input, ImportSettings(RestoreMode.Merge))

        assertSuccess(result).transform { it.entries }.all {
            hasSize(1)
            first().isInstanceOf<PreferenceRestoreEntry>().all {
                prop(PreferenceRestoreEntry::preference).transform { it.key }.isEqualTo("PREFERENCE_VALUE_SET_NON_DEFAULT")
                prop(PreferenceRestoreEntry::hasStoredValue).isEqualTo(true)
                prop(PreferenceRestoreEntry::result).isEqualTo(RestoreResult.Skipped)
            }
        }
    }
}
