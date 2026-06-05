package app.linksheet.feature.backup.impl.core

import android.util.Log
import app.linksheet.feature.backup.api.ExportModel
import app.linksheet.feature.backup.api.ExportableRepository
import app.linksheet.feature.backup.api.UnsupportedExportModel
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.PreferenceRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

data class BackupConfiguration(
    val builder: PolymorphicModuleBuilder<ExportModel>.() -> Unit,
    val holders: List<PreferenceExportImportHolder<*>>,
    val databaseHolders: List<DatabaseExportImportHolder<*, *>>,
) {
    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(ExportModel::class) {
                builder(this)
                defaultDeserializer { UnsupportedExportModel.serializer() }
            }
        }
    }
}

data class PreferenceExportImportHolder<T : PreferenceDefinition>(
    val type: PreferenceType,
    val repository: PreferenceRepository,
    val definition: T,
    val exclude: Set<String> = emptySet()
) {
    val preferences: Map<String, Preference<*, *>>
        get() = definition.all

    fun migrate() {
        definition.runMigrations(repository)
    }
}

data class DatabaseExportImportHolder<Entity, T : ExportModel>(
    val type: ExportType,
    val repository: ExportableRepository<Entity, T>
)
