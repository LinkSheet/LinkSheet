package fe.linksheet.module.statistic

import androidx.lifecycle.Lifecycle
import fe.linksheet.extension.koin.service
import fe.linksheet.module.lifecycle.Service
import fe.linksheet.module.preference.AppPreferenceRepository
import org.koin.dsl.module

val statisticsModule = module {
    service<Statistics, AppPreferenceRepository> { _, preferences ->
        Statistics(preferences)
    }
}

class Statistics(val preferenceRepository: AppPreferenceRepository) : Service {
    private val startedAt = System.currentTimeMillis()

//    fun stop(): Long {
//        return System.currentTimeMillis() - start
//    }

    //    companion object {
//        fun startTimer(): Statistics {
//            return Statistics(System.currentTimeMillis())
//        }
//    }
    override fun shutdown(lifecycle: Lifecycle) {
        TODO("Not yet implemented")
    }
}
