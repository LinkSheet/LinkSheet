package fe.embed.resolve.config

import com.google.gson.annotations.SerializedName

public data class ConfigV1(val services: List<ServiceV1>) : Config

public data class ServiceV1(
    val name: String,
    val domain: String,
    val pattern: Regex,
    @SerializedName(value = "ignore_pattern") val ignorePattern: Regex? = null,
    @SerializedName(value = "embed_domains") val embedDomains: Set<String>,
)
