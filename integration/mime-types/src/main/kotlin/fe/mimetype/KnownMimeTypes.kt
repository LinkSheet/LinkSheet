package fe.mimetype

import fe.linksheet.mimetype.ApacheTikaMimeTypes

public object KnownMimeTypes {
    public fun getExtensionsForMimeTypeOrNull(mimeType: String): Array<String>? {
        return ApacheTikaMimeTypes.MAP[mimeType]
    }

    public fun getMimeTypeForExtensionOrNull(extension: String): String? {
        return extensionToMimeType[extension]
    }

    private val extensionToMimeType by lazy {
        Util.createReverseMap(ApacheTikaMimeTypes.MAP)
    }

    public fun findKnownExtensions(fileName: String): List<FoundExtension> {
        val known = mutableListOf<FoundExtension>()
        // test.tar.gz -> [.tar.gz, .gz]
        // test.zip -> [.zip]
        var dotIdx = fileName.indexOf('.')
        while (dotIdx != -1) {
            val currentExtension = fileName.substring(dotIdx + 1)
            if (currentExtension.isNotEmpty()) {
                val mimeType = getMimeTypeForExtensionOrNull(currentExtension)
                if (mimeType != null) {
                    val fileNameOnly = fileName.substring(0, dotIdx)
                    known.add(FoundExtension(fileNameOnly, currentExtension, mimeType))
                }
            }

            dotIdx = fileName.indexOf('.', dotIdx + 1)
        }

        return known
    }
}

public data class FoundExtension(
    val fileNameOnly: String,
    val extension: String,
    val mimeType: String
)
