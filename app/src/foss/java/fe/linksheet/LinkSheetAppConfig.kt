package fe.linksheet

import fe.linksheet.config.AppConfig

object LinkSheetAppConfig : AppConfig {
    override fun showDonationBanner(): Boolean {
        return true
    }
}