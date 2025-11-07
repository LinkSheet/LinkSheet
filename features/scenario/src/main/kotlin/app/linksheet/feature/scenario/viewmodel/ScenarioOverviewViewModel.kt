package app.linksheet.feature.scenario.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.engine.database.repository.ScenarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

class ScenarioOverviewViewModel(
    private val context: Application,
    private val repository: ScenarioRepository
) : ViewModel() {

    fun createScenario(name: String) = viewModelScope.async(Dispatchers.IO) {
        repository.createScenario(name)
    }

    fun getAll(): Flow<List<Scenario>> {
        return repository.getAllScenarios()
    }

    fun move(from: Scenario, to: Scenario) = viewModelScope.async(Dispatchers.IO) {
        repository.move(from, to)
    }
}
