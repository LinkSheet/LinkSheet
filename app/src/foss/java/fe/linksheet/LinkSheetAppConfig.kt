package fe.linksheet

import fe.linksheet.config.AppConfig

object LinkSheetAppConfig : AppConfig {
    override fun isPro(): Boolean {
        return false
    }

    override fun showDonationBanner(): Boolean {
        return !isPro()
    }

    override fun supabaseHost(): String = ""
    override fun supabaseApiKey(): String = ""
}