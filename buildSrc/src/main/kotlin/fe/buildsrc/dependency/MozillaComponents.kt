package fe.buildsrc.dependency

import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotation
import org.gradle.kotlin.dsl.IsNotADependency

object MozillaComponents : DependencyGroup(group = "org.mozilla.components") {
    val concept = Concept

    object Concept : IsNotADependency {
        val storage = DependencyNotation(group = group, name = "concept-storage")
        val toolbar = DependencyNotation(group = group, name = "concept-toolbar")
    }

    val browser = Browser

    object Browser : IsNotADependency {
        val storageSync = DependencyNotation(group = group, name = "browser-storage-sync")
    }

    val service = Service

    object Service : IsNotADependency {
        val firefoxAccounts = DependencyNotation(group = group, name = "service-firefox-accounts")
        val syncLogins = DependencyNotation(group = group, name = "service-sync-logins")
        val syncAutofill = DependencyNotation(group = group, name = "service-sync-autofill")
    }

    val support = Support

    object Support : IsNotADependency {
        val rustLog = DependencyNotation(group = group, name = "support-rustlog")
        val rustHttp = DependencyNotation(group = group, name = "support-rusthttp")
        val utils = DependencyNotation(group = group, name = "support-utils")
    }

    val lib = Lib

    object Lib : IsNotADependency {
        val fetchHttpUrlConnection = DependencyNotation(group = group, name = "lib-fetch-httpurlconnection")
        val dataProtect = DependencyNotation(group = group, name = "lib-dataprotect")
        val publicSuffixList = DependencyNotation(group = group, name = "lib-publicsuffixlist")
    }
}
