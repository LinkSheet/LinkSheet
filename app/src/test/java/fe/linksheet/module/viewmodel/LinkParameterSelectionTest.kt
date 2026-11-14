package fe.linksheet.module.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class LinkParameterSelectionTest {
    @Test
    fun keepsInstagramImageIndexAndRemovesTracking() {
        val selection = LinkParameterSelection.from("https://www.instagram.com/p/DcejiwAiFRd/?img_index=4&igsi=MTNpb2F4aHYzN2VyOA==")
        assertEquals("https://www.instagram.com/p/DcejiwAiFRd/?img_index=4", selection.select(1, false).url)
    }

    @Test
    fun preservesEncodingRepeatedNamesEmptyValuesAndFragment() {
        val url = "https://example.com/p?a=%26%3d+%20&a=second&flag&empty=&tracking=x#part?x=y"
        val selection = LinkParameterSelection.from(url)
        assertEquals(url, selection.url)
        assertEquals("https://example.com/p?a=%26%3d+%20&flag&empty=#part?x=y", selection.select(1, false).select(4, false).url)
        assertEquals("https://example.com/p#part?x=y", selection.selectAll(false).url)
        assertEquals(url, selection.selectAll(false).selectAll(true).url)
    }

    @Test
    fun canRestoreOneParameterAfterDeselectingAll() {
        val selection = LinkParameterSelection.from("https://example.com/?img_index=4&igsi=abc")
        assertEquals("https://example.com/?img_index=4", selection.selectAll(false).select(0, true).url)
    }

    @Test
    fun ignoresQuestionMarksInFragmentsAndLeavesLinksWithoutParametersAlone() {
        for (url in listOf("https://example.com/path", "https://example.com/#part?x=y", "https://example.com/?#part")) {
            val selection = LinkParameterSelection.from(url)
            assertEquals(emptyList<String>(), selection.parameters)
            assertEquals(url, selection.selectAll(false).url)
        }
    }
}
