package fe.linksheet.module.preference.state

import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments

sealed interface AppStateUpdate {
    fun execute(experimentsRepository: ExperimentRepository)
}

object NewDefaults2024_11_29 : AppStateUpdate {
    override fun execute(experimentsRepository: ExperimentRepository) {
        experimentsRepository.put(Experiments.loopDetector, true)
    }
}

object NewDefaults2024_11_30 : AppStateUpdate {
    override fun execute(experimentsRepository: ExperimentRepository) {
        experimentsRepository.put(Experiments.editClipboard, true)
    }
}


object NewDefaults2024_12_16 : AppStateUpdate {
    override fun execute(experimentsRepository: ExperimentRepository) {
        experimentsRepository.put(Experiments.improvedIntentResolver, true)
        experimentsRepository.put(Experiments.interceptAccidentalTaps, true)
    }
}
