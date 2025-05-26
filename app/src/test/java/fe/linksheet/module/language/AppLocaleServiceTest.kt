package fe.linksheet.module.language

import androidx.core.os.LocaleListCompat
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.*
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.result.assert.assertSuccess
import fe.std.test.tableTest
import kotlinx.coroutines.test.runTest
import java.util.*
import kotlin.test.Test

internal class AppLocaleServiceTest : BaseUnitTest {
    companion object {
        private fun createLocale(language: String, region: String): Locale.Builder {
            return Locale.Builder()
                .setLanguage(language)
                .setRegion(region)
                .setExtension(Locale.UNICODE_LOCALE_EXTENSION, "fw-mon-mu-celsius")
        }

        private val ZH_CN_HANS = createLocale("zh", "CN").setScript("Hans").build()
        private val EN_US = createLocale("en", "US").build()
        private val IT_IT = createLocale("it", "IT").build()
    }

    @Test
    fun `test locale changing`() {
        val systemLocales = LocaleListCompat.create(EN_US, ZH_CN_HANS, IT_IT)
        var perAppLocales = LocaleListCompat.getEmptyLocaleList()

        val service = AppLocaleService(
            getDefaultLocaleList = { systemLocales },
            _supportedLocales = listOf("en", "fr", "ja"),
            setApplicationLocales = {
                perAppLocales = it
            },
            getApplicationLocales = { perAppLocales }
        )

        assertThat(service.hasPerAppLocale()).isFalse()
        service.update(LocaleListCompat.create(Locale.ENGLISH))
        assertThat(perAppLocales)
            .prop(LocaleListCompat::size)
            .isEqualTo(1)
        assertThat(service.hasPerAppLocale()).isTrue()
    }

    @Test
    fun `test isEquivalent`() {
        val result = AppLocaleService.isEquivalent(EN_US, Locale.ENGLISH)
        assertThat(result).isTrue()
    }

    @Test
    fun test() = runTest {
        val service = AppLocaleService(
            getDefaultLocaleList = { LocaleListCompat.create(Locale.ENGLISH, Locale.FRENCH, Locale.JAPANESE) },
            _supportedLocales = listOf("en", "fr", "ja"),
            setApplicationLocales = {
            },
            getApplicationLocales = {
                LocaleListCompat.create(Locale.FRENCH, Locale.JAPANESE)
            }
        )

        assertThat(service.getCurrentDeviceLocale()).transform { it.language }.isEqualTo("fr")
        assertThat(service.getCurrentAppLocaleItem()).transform { it.locale.language }.isEqualTo("en")
        service.localesFlow.test {
            assertThat(awaitItem())
                .extracting { it.item.locale to it.isDeviceLanguage }
                .containsExactly(Locale.ENGLISH to false, Locale.FRENCH to true, Locale.JAPANESE to false)

            awaitComplete()
        }
    }

    @Test
    fun `test locale fixing`() = tableTest<String, String>("locale", "expected")
        .row("en", "en")
        .row("de", "de")
        .row("es", "es")
        .row("es-rMX", "es-MX")
        .row("b+sr+Latn", "sr-Latn")
        .row("zh-rTW", "zh-TW")
        .test2<String, String> {
            AppLocaleService.fixLocale(it)
        }
        .forAll { input, expected ->
            assertThat(runTest(input)).isEqualTo(expected)
        }

    @Test
    fun `test locale parsing`() {
        val result = AppLocaleService.parseLocale("zh-Hans-CN-u-fw-mon-mu-celsius")
        assertSuccess(result).isEqualTo(ZH_CN_HANS)
    }

    @Test
    fun `test isDeviceLocale`() = tableTest<Locale, Locale>("system locale", "app locale")
        .row(Locale.ENGLISH, Locale.ENGLISH)
        .row(EN_US, Locale.ENGLISH)
        .row(ZH_CN_HANS, Locale("zh", "CN"))
        .test2<Pair<Locale, Locale>, Boolean> { (systemLocale, appLocale) ->
            AppLocaleService.isEquivalent(systemLocale, appLocale)
        }
        .forAll { systemLocale, appLocale ->
            assertThat(runTest(systemLocale to appLocale)).isTrue()
        }
}

