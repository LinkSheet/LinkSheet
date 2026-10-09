package app.linksheet.feature.backup.impl.core

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

fun BackupConfiguration(
    preferenceBackups: List<PreferenceRepositoryBackup<*>> = emptyList(),
    databaseBackups: List<DatabaseBackup<*, *>> = emptyList(),
    configureSerialization: PolymorphicModuleBuilder<ExportModel>.() -> Unit = {}
): BackupConfiguration {
    return BackupConfiguration(
        json = Json {
            serializersModule = SerializersModule {
                polymorphic(BackupSchema::class) {
                    defaultDeserializer { LegacyBackupSchema.serializer() }
                }
                polymorphic(ExportModel::class) {
                    configureSerialization(this)
                    defaultDeserializer { UnsupportedExportModel.serializer() }
                }
            }
        },
        preferenceBackups = preferenceBackups,
        databaseBackups = databaseBackups,
    )
}

data class BackupConfiguration(
    val json: Json,
    val preferenceBackups: List<PreferenceRepositoryBackup<*>>,
    val databaseBackups: List<DatabaseBackup<*, *>>,
)

data class PreferenceRepositoryBackup<T : PreferenceDefinition>(
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

data class DatabaseBackup<Entity, T : ExportModel>(
    val type: BackupType,
    val repository: ExportableRepository<Entity, T>
)
