package fe.embed.resolve.manifest


public data class Manifest(val versions: List<ManifestVersion>)

public data class ManifestVersion(val latest: String, val version: String)
