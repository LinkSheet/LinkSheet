import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.plugin.use.PluginDependency
import org.gradle.plugin.use.PluginDependencySpec

fun PluginDependenciesSpecScope.id(plugin: Provider<PluginDependency>): PluginDependencySpec {
    return id(plugin.get().pluginId)
}
