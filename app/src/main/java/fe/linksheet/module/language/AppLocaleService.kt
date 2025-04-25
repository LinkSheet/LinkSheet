package fe.linksheet.module.language

import android.os.Build
import android.os.Parcelable
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import fe.kotlin.extension.string.capitalize
import fe.linksheet.BuildConfig
import fe.linksheet.util.flowOfLazy
import fe.std.result.IResult
import fe.std.result.getOrNull
import fe.std.result.tryCatch
import kotlinx.coroutines.flow.combine
import kotlinx.parcelize.Parcelize
import org.koin.dsl.module
import java.util.*


val AppLocaleModule = module {
    single { AndroidAppLocaleService() }
}

@Suppress("FunctionName")
internal fun AndroidAppLocaleService(): AppLocaleService {
    return AppLocaleService(
        getDefaultLocaleList = { LocaleListCompat.getAdjustedDefault() },
        _supportedLocales = BuildConfig.SUPPORTED_LOCALES.toList(),
        setApplicationLocales = { AppCompatDelegate.setApplicationLocales(it) },
        getApplicationLocales = { AppCompatDelegate.getApplicationLocales() }
    )
}

class AppLocaleService(
    private val getDefaultLocaleList: () -> LocaleListCompat,
    private val _supportedLocales: List<String>,
    private val setApplicationLocales: (LocaleListCompat) -> Unit,
    private val getApplicationLocales: () -> LocaleListCompat,
) {
    companion object {
        fun parseLocale(locale: String): IResult<Locale> {
            return tryCatch { Locale.forLanguageTag(fixLocale(locale)) }
        }

        fun fixLocale(locale: String): String {
            return locale
                .replace("-r", "-")
                .replace("^b\\+".toRegex(), "")
                .replace("+", "-")
        }

        fun isEquivalent(deviceLocale: Locale?, appLocale: Locale): Boolean {
            if (deviceLocale == null) return false
            val strippedSystemLocale = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> deviceLocale.stripExtensions()
                else -> deviceLocale
            }

            return when {
                appLocale.country.isNotEmpty() && strippedSystemLocale.country != appLocale.country -> false
                appLocale.variant.isNotEmpty() && strippedSystemLocale.variant != appLocale.variant -> false
                appLocale.script.isNotEmpty() && strippedSystemLocale.script != appLocale.script -> false
                else -> strippedSystemLocale.language == appLocale.language
            }
        }
    }

    private val supportedLocales by lazy {
        _supportedLocales
            .mapNotNull { parseLocale(it).getOrNull() }
            .map { createLocaleItem(it) }
    }

    val deviceLocaleFlow = flowOfLazy { getCurrentDeviceLocale() }
    val appLocaleItemFlow = flowOfLazy { getCurrentAppLocaleItem() }

    val currentLocaleFlow = appLocaleItemFlow.combine(deviceLocaleFlow) { appLocaleItem, deviceLocale ->
        appLocaleItem to isEquivalent(deviceLocale, appLocaleItem.locale)
    }

    val localesFlow = appLocaleItemFlow.combine(deviceLocaleFlow) { appLocaleItem, deviceLocale ->
        createLocales(appLocaleItem.locale, deviceLocale)
    }

    private fun createLocales(appLocale: Locale, deviceLocale: Locale): List<DisplayLocaleItem> {
        return supportedLocales
            .map { it.toDisplay(appLocale, deviceLocale) }
            .sortedBy { it.currentLocaleName }
            .sortedBy { it.getSortIndex(appLocale) }
    }

    private fun LocaleItem.toDisplay(appLocale: Locale, deviceLocale: Locale): DisplayLocaleItem {
        return DisplayLocaleItem(
            this,
            locale.getDisplayLanguage(appLocale),
            isEquivalent(deviceLocale, locale)
        )
    }

    private fun DisplayLocaleItem.getSortIndex(appLocale: Locale): Int {
        return when {
            item.locale == appLocale -> -1
            isDeviceLanguage -> 0
            else -> 1
        }
    }

    fun getCurrentDeviceLocale(): Locale {
        val list = getDefaultLocaleList()
        val index = if (hasPerAppLocale()) 1 else 0

        return list.get(index) ?: Locale.ENGLISH
    }

    fun update(it: LocaleItem) {
        update(LocaleListCompat.create(it.locale))
    }

    fun update(list: LocaleListCompat) {
        setApplicationLocales(list)
    }

    fun getCurrentAppLocaleItem(): LocaleItem {
        val list = getDefaultLocaleList()
        val locale = list.takeIf { it.size() > 0 }?.get(0) ?: Locale.ENGLISH
        return createLocaleItem(locale)
    }

    private fun createLocaleItem(locale: Locale): LocaleItem {
        val displayName = locale.getDisplayLanguage(locale).capitalize(locale)
        return LocaleItem(locale, displayName)
    }

    fun hasPerAppLocale(): Boolean {
        return !getApplicationLocales().isEmpty
    }
}


@Parcelize
data class LocaleItem(
    val locale: Locale,
    val displayName: String,
) : Parcelable

data class DisplayLocaleItem(
    val item: LocaleItem,
    val currentLocaleName: String,
    val isDeviceLanguage: Boolean,
)
