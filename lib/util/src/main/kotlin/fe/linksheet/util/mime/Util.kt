package fe.linksheet.util.mime

object Util {
    fun createReverseMap(mimeTypeToExtensions: Map<String, Array<String>>): Map<String, String> {
        val extensionToMimeType = mutableMapOf<String, String>()
        for ((mimeType, extensions) in mimeTypeToExtensions) {
            for (it in extensions) {
                extensionToMimeType.put(it, mimeType)
            }
        }

        return extensionToMimeType
    }
}
