package fe.mimetype

import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.std.test.tableTest
import kotlin.test.Test

internal class KnownMimeTypesTest {
    @Test
    fun `test findKnownExtensions`() {
        tableTest<String, List<FoundExtension>>("fileName", "extensions")
            .row("test.tar.gz", listOf(
                FoundExtension("test", "tar.gz", "application/x-gtar"),
                FoundExtension("test.tar", "gz", "application/gzip"),
            ))
            .row("test.gz", listOf(
                FoundExtension("test", "gz", "application/gzip"))
            )
            .row("test.tar.bz2", listOf(
                FoundExtension("test", "tar.bz2", "application/x-gtar"),
                FoundExtension("test.tar", "bz2", "application/x-bzip2"),
            ))
            .row("test.", emptyList())
            .row("test", emptyList())
            .row(".test.", emptyList())
            .row("test.yeet.jpg", listOf(
                FoundExtension("test.yeet", "jpg","image/jpeg")
            ))
            .test2<String, List<FoundExtension>> {
                KnownMimeTypes.findKnownExtensions(it)
            }
            .forAll { fileName, mimeTypes ->
                assertThat(runTest(fileName)).isEqualTo(mimeTypes)
            }

    }
}
