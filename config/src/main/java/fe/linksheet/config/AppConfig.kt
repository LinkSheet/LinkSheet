package fe.linksheet.config

interface AppConfig {
    fun isPro(): Boolean
    fun showDonationBanner(): Boolean
    fun supabaseHost(): String

    fun supabaseApiKey(): String
}