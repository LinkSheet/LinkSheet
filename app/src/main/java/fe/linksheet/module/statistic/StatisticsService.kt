package fe.linksheet.module.statistic

import androidx.lifecycle.LifecycleOwner
import com.google.gson.*
import fe.android.lifecycle.LifecycleAwareService
import fe.gson.extension.json.array.elementsOrNull
import fe.gson.extension.json.element.arrayOrNull
import fe.gson.extension.json.`object`.asIntOrNull
import fe.linksheet.BuildConfig
import fe.linksheet.extension.koin.service
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.util.LinkSheetInfo
import fe.linksheet.util.BuildInfo
import org.koin.dsl.module

val statisticsModule = module {
    service<StatisticsService, AppPreferenceRepository> { _, preferences ->
        val gson = scope.get<Gson>().newBuilder().setFormattingStyle(FormattingStyle.COMPACT).create()

        StatisticsService(preferences, gson)
    }
}

class StatisticsService(val preferenceRepository: AppPreferenceRepository, val gson: Gson) : LifecycleAwareService {
    private val lastVersionsService by lazy {
        LastVersionService(gson, LinkSheetInfo.buildInfo)
    }

    private val start = System.currentTimeMillis()

    @OptIn(SensitivePreference::class)
    override suspend fun onStop() {
        val currentUseTime = preferenceRepository.get(AppPreferences.useTimeMs)
        val usedFor = System.currentTimeMillis() - start

        preferenceRepository.edit {
            put(AppPreferences.useTimeMs, currentUseTime + usedFor)
            put(AppPreferences.lastVersion, BuildConfig.VERSION_CODE)
        }
    }

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        val lastVersions = preferenceRepository.get(AppPreferences.lastVersions)
        lastVersionsService.handleVersions(lastVersions)?.let {
            preferenceRepository.edit {
                put(AppPreferences.lastVersions, it)
            }
        }
    }
}

// TODO: Find a better way to do this
class LastVersionService(
    private val gson: Gson,
    private val buildInfo: BuildInfo,
) {
    fun handleVersions(lastVersions: String?): String? {
        val lastVersionArray = runCatching { parseLastVersions(lastVersions) }.getOrDefault(JsonArray())
        val lastVersionCodes = runCatching { parseVersionCodes(lastVersionArray) }.getOrDefault(emptySet())

        if (!lastVersionCodes.contains(buildInfo.versionCode)) {
            lastVersionArray.add(gson.toJsonTree(buildInfo))

            return gson.toJson(lastVersionArray)
        }

        return null
    }

    private fun parseLastVersions(lastVersions: String?): JsonArray {
        val versions = JsonParser.parseString(lastVersions).arrayOrNull()
        return versions ?: JsonArray()
    }

    private fun parseVersionCodes(versions: JsonArray?): Set<Int> {
        val lastVersionCodes = versions
            ?.elementsOrNull<JsonObject>(keepNulls = false)
            ?.mapNotNull { it?.asIntOrNull("version_code") }
            ?.toSet()

        return lastVersionCodes ?: emptySet()
    }
}
