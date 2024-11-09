package fe.buildlogic.extension

import fe.buildlogic.KotlinClosure2
import fe.buildlogic.KotlinClosure4
import net.nemerosa.versioning.ReleaseInfo
import net.nemerosa.versioning.SCMInfo
import net.nemerosa.versioning.VersionInfo
import net.nemerosa.versioning.VersioningExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider

typealias ReleaseMode = (nextTag: String?, lastTag: String?, currentTag: String?, VersioningExtension) -> String
typealias ReleaseParser = (info: SCMInfo, branchTypeSeparator: String) -> ReleaseInfo

val CurrentTagMode: ReleaseMode = { _, _, currentTag, _ ->
    currentTag ?: "0.0.0"
}

val TagReleaseParser: ReleaseParser = { info, _ ->
    ReleaseInfo("release", info.tag)
}

val ReleaseMode.closure: KotlinClosure4<String?, String?, String?, VersioningExtension, String>
    get () = KotlinClosure4(this)

val ReleaseParser.closure: KotlinClosure2<SCMInfo, String, ReleaseInfo>
    get () = KotlinClosure2(this)


typealias VersionStrategy<T> = (VersionInfo) -> T

val DefaultVersionStrategy: VersionStrategy<String> = { info ->
    runCatching { info.tag ?: info.full }.getOrDefault("0.0.0")
}

fun <T> VersioningExtension.asProvider(
    project: Project,
    strategy: VersionStrategy<T>,
): Provider<T> {
    return project.provider {
        val info = computeInfo()
        strategy(info)
    }
}
