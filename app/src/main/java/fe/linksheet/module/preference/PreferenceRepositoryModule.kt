package fe.linksheet.module.preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.tasomaniac.openwith.resolver.BrowserHandler
import fe.linksheet.ui.theme.Theme
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    single {
        PreferenceRepository(get())
    }
}

typealias StringPersister<T> = (T) -> String
typealias StringReader<T> = (String) -> T?

typealias IntPersister<T> = (T) -> Int
typealias IntReader<T> = (Int) -> T?

class PreferenceRepository(context: Context) {
    private val preferences by lazy {
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    companion object {
        val enableCopyButton = Preference("enable_copy_button", false)
        val hideAfterCopying = Preference("hide_after_copying", true)
        val singleTap = Preference("single_tap", false)
        val usageStatsSorting = Preference("usage_stats_sorting", false)
        val browserMode = Preference<BrowserHandler.BrowserMode>(
            "browser_mode", BrowserHandler.BrowserMode.AlwaysAsk
        )
        val selectedBrowser = Preference<String>("selected_browser", null)
        val enableSendButton = Preference("enable_send_button", false)
        val alwaysShowPackageName = Preference("always_show_package_name", false)
        val disableToasts = Preference("disable_toasts", false)
        val gridLayout = Preference("grid_layout", false)
        val useClearUrls = Preference("use_clear_urls", false)
        val followRedirects = Preference("follow_redirects", false)
        val followRedirectsExternalService = Preference("follow_redirects_external_service", false)
        val followOnlyKnownTrackers = Preference("follow_only_known_trackers", true)
        val theme = Preference("theme", Theme.System)


        val all = listOf(
            enableCopyButton,
            singleTap,
            usageStatsSorting,
            browserMode,
            selectedBrowser,
            enableSendButton,
            alwaysShowPackageName,
            disableToasts,
            gridLayout,
            useClearUrls,
            followRedirects,
            followRedirectsExternalService,
            followOnlyKnownTrackers,
            theme
        )
    }

    data class Preference<T>(val key: String, val default: T?)

    fun editor(edit: SharedPreferences.Editor.() -> Unit) {
        preferences.edit().apply(edit).apply()
    }

    fun getBoolean(preference: Preference<Boolean>): Boolean? {
        if (!this.preferences.contains(preference.key)) return preference.default
        return this.preferences.getBoolean(preference.key, false)
    }

    fun writeBoolean(preference: Preference<Boolean>, newState: Boolean) = editor {
        this.putBoolean(preference.key, newState)
    }

    fun getInt(preference: Preference<Int>): Int? {
        if (!this.preferences.contains(preference.key)) return preference.default
        return this.preferences.getInt(preference.key, -1)
    }

    fun writeInt(preference: Preference<Int>, newState: Int) = editor {
        this.putInt(preference.key, newState)
    }

    fun <T> writeInt(preference: Preference<T>, newState: T, persister: IntPersister<T>) =
        editor {
            this.putInt(preference.key, persister(newState))
        }

    fun <T> getInt(
        preference: Preference<T>,
        persister: IntPersister<T>,
        reader: IntReader<T>
    ): T? {
        return this.preferences
            .getInt(preference.key, preference.default?.let(persister) ?: -1)
            .let {
                if (it == -1) null
                else reader(it)
            }
    }

    fun getLong(preference: Preference<Long>): Long? {
        if (!this.preferences.contains(preference.key)) return preference.default
        return this.preferences.getLong(preference.key, -1L)
    }

    fun writeLong(preference: Preference<Long>, newState: Long) = editor {
        this.putLong(preference.key, newState)
    }

    fun getString(preference: Preference<String>): String? {
        return this.preferences.getString(preference.key, preference.default)
    }

    fun writeString(preference: Preference<String>, newState: String?) = editor {
        this.putString(preference.key, newState)
    }

    fun <T> writeString(preference: Preference<T>, newState: T, persister: StringPersister<T>) =
        editor {
            this.putString(preference.key, persister(newState))
        }

    fun <T> getString(
        preference: Preference<T>,
        persister: StringPersister<T>,
        reader: StringReader<T>
    ): T? {
        return this.preferences
            .getString(preference.key, preference.default?.let(persister) ?: "")
            .let {
                if (it == null) null
                else reader(it)
            }
    }

    fun clearAll() = editor {
        all.forEach {
            this.remove(it.key)
        }
    }
}

fun SharedPreferences.Editor.writeBoolean(
    preference: PreferenceRepository.Preference<Boolean>,
    newState: Boolean
) {
    this.putBoolean(preference.key, newState)
}

fun SharedPreferences.Editor.writeInt(
    preference: PreferenceRepository.Preference<Int>,
    newState: Int
) {
    this.putInt(preference.key, newState)
}

fun SharedPreferences.Editor.writeLong(
    preference: PreferenceRepository.Preference<Long>,
    newState: Long
) {
    this.putLong(preference.key, newState)
}

fun SharedPreferences.Editor.writeString(
    preference: PreferenceRepository.Preference<String>,
    newState: String
) {
    this.putString(preference.key, newState)
}