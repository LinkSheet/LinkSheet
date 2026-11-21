package app.linksheet.feature.clearurls.core

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

internal class QueryParametersTest {
    @Test
    fun `query range covers the parameters but neither path nor fragment`() {
        val url = "https://example.com/p/ABC/?foo=bar&baz#part"
        assertEquals("?foo=bar&baz", url.substring(queryRange(url)!!))
    }

    @Test
    fun `urls without a query have no range`() {
        assertNull(queryRange("https://example.com/p/ABC/"))
        // A question mark which only appears in the fragment does not start a query
        assertNull(queryRange("https://example.com/p/ABC/#part?value"))
    }

    @Test
    fun `removal keeps path and fragment intact`() {
        assertEquals(
            "https://example.com/p/ABC/#part?value",
            removeAllQueryParameters("https://example.com/p/ABC/?igsh=tracking&x=1#part?value")
        )
        assertEquals(
            "https://example.com/p/ABC/",
            removeAllQueryParameters("https://example.com/p/ABC/?igsh=tracking")
        )
    }

    @Test
    fun `urls without a query are returned unchanged`() {
        for (url in listOf("https://example.com/p/ABC/", "https://example.com/p/ABC/#part?value")) {
            assertEquals(url, removeAllQueryParameters(url))
        }
    }
}
