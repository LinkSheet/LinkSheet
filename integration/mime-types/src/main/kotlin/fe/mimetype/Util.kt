package fe.mimetype

public object Util {
    public fun createReverseMap(mimeTypeToExtensions: Map<String, Array<String>>): Map<String, String> {
        val extensionToMimeType = mutableMapOf<String, String>()
        for ((mimeType, extensions) in mimeTypeToExtensions) {
            for (it in extensions) {
                extensionToMimeType[it] = mimeType
            }
        }

        return extensionToMimeType
    }
}
