package fe.linksheet.module.preference

import android.content.Context
import android.content.SharedPreferences
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    single {
        PreferenceRepository(get())
    }
}

class PreferenceRepository(context: Context) {
    private val preferences by lazy {
        context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    companion object {
        val enableCopyButton = Preference("enable_copy_button", false)
        val singleTap = Preference("single_tap", false)

        val all = listOf(
            enableCopyButton,
            singleTap
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