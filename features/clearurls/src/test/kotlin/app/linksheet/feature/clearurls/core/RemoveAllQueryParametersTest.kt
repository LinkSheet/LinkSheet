package app.linksheet.feature.clearurls.core

import fe.clearurlskt.Modification
import fe.clearurlskt.config.provider.ClearUrlBundleSerializer
import fe.clearurlskt.config.provider.ClearUrlV1Bundle
import fe.clearurlskt.config.provider.ProviderV1
import org.junit.Test
import org.junit.Assert.assertEquals

internal class RemoveAllQueryParametersTest {
    private val cleaner = ConfiguredClearUrls(
        (ClearUrlBundleSerializer.decodeBundle(
            java.io.File("src/main/res/raw/clearurls.bin").readBytes()
        ) as ClearUrlV1Bundle).providers
    )
    private val instagram = setOf("instagram")

    @Test
    fun `selection removes unknown repeated and empty parameters and records their names`() {
        val (url, operations) = cleaner.clearUrl(
            "https://www.instagram.com/p/ABC/?custom=one&custom=two&empty=&flag#comments", removeAllQueryProviders = instagram
        )
        assertEquals("https://www.instagram.com/p/ABC/#comments", url)
        val removal = operations.filterIsInstance<Modification.ParameterRemoval>().last()
        assertEquals("instagram", removal.provider)
        assertEquals(setOf("custom", "empty", "flag"), removal.fields)
        assertEquals(emptySet<String>(), removal.fragment)
    }

    @Test
    fun `deselecting a provider restores normal cleaning on the same instance`() {
        val input = "https://www.instagram.com/p/ABC/?igsh=tracking&custom=keep"
        assertEquals("https://www.instagram.com/p/ABC/", cleaner.clearUrl(input, removeAllQueryProviders = instagram).first)
        assertEquals("https://www.instagram.com/p/ABC/?custom=keep", cleaner.clearUrl(input).first)
    }

    @Test
    fun `unselected providers keep functional parameters`() {
        assertEquals(
            "https://example.com/?custom=keep",
            cleaner.clearUrl("https://example.com/?custom=keep&utm_source=tracking", removeAllQueryProviders = instagram).first
        )
    }

    @Test
    fun `instagram redirect to another provider preserves destination parameters`() {
        assertEquals(
            "https://example.com/?id=42",
            cleaner.clearUrl("https://l.instagram.com/?u=https%3A%2F%2Fexample.com%2F%3Fid%3D42&e=tracking", removeAllQueryProviders = instagram).first
        )
    }

    @Test
    fun `redirect into selected provider removes destination parameters`() {
        assertEquals(
            "https://www.instagram.com/p/ABC/",
            cleaner.clearUrl("https://www.google.com/url?q=https%3A%2F%2Fwww.instagram.com%2Fp%2FABC%2F%3Fcustom%3D42", removeAllQueryProviders = instagram).first
        )
    }

    @Test
    fun `explicit override applies even to provider exceptions and preserves exact fragments`() {
        val provider = ProviderV1(
            sortPosition = 0, key = "custom", url = Regex("^https://example\\.com/"),
            completeProvider = false, rules = emptyList(), rawRules = emptyList(),
            referralMarketing = emptyList(), exceptions = listOf(Regex(".*")), redirections = emptyList()
        )
        val customCleaner = ConfiguredClearUrls(listOf(provider))
        for (input in listOf("https://example.com/?#part?value", "https://example.com/?foo=bar#part?value")) {
            assertEquals("https://example.com/#part?value", customCleaner.clearUrl(input, removeAllQueryProviders = setOf("custom")).first)
            assertEquals(input, customCleaner.clearUrl(input).first)
        }
        val fragmentOnly = "https://example.com/#part?value"
        assertEquals(fragmentOnly, customCleaner.clearUrl(fragmentOnly, removeAllQueryProviders = setOf("custom")).first)
    }
}
