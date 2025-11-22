@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.engine.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.feature.engine.database.entity.ExpressionRule
import app.linksheet.feature.engine.database.entity.ExpressionRuleType
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.engine.database.repository.ScenarioRepository
import app.linksheet.feature.engine.eval.BundleSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class ScenarioViewModel(
    private val context: Application,
    private val scenarioRepository: ScenarioRepository,
    private val id: Long,
) : ViewModel() {
    fun getScenario(): Flow<Scenario> {
        return scenarioRepository.getById(id)
    }

    fun getScenarioExpressions(): Flow<Pair<Scenario, List<ExpressionRule>>?> {
        return scenarioRepository.getScenarioExpressionsById(id)
    }

    fun toString(rule: ExpressionRule): String {
        return scenarioRepository.toString(rule)
    }

    fun save(rule: String) = viewModelScope.launch(Dispatchers.IO) {
        val bundle = BundleSerializer.decodeFromHexString(rule)
        val expression = scenarioRepository.insertExpression(bundle, ExpressionRuleType.Post)
        scenarioRepository.insertScenarioExpression(id, expression)
    }
}
