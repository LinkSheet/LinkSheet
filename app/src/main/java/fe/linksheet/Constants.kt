package fe.linksheet

import fe.linksheet.util.AppSignature

const val openLinkWithGithub = "https://github.com/tasomaniac/OpenLinkWith"
const val mastodonRedirectGithub = "https://github.com/zacharee/MastodonRedirect"
const val linksheetGithub = "https://github.com/1fexd/LinkSheet"
const val workflowIdPath = "actions/runs"

const val discordInvite = "https://discord.gg/GVNphPrQPf"

const val donationBuyMeACoffee = "https://www.buymeacoffee.com/1fexd"
const val donationCrypto = "https://coindrop.to/fexd"
const val linkSheetCompatGithubReleases = "https://github.com/1fexd/LinkSheetCompat/releases"

const val shizukuDownload = "https://shizuku.rikka.app/download"

const val donationBannerAfterMinutes = 10
const val developmentTimeHours = 370
const val developmentTimeMonths = 11

val officialSigningKeys = mapOf(
    "C2A8B18C328DFB39C896491757ED11C145D3ACCA43212FA3DE362433C416AAA9" to AppSignature.BuildType.Manual,
    "3FCF7675BC90E239892C7262499DCC9F8CE6A52B7E58D02B56AA60CA669D6701" to AppSignature.BuildType.GithubPipeline,
)

val lineSeparator: String = System.lineSeparator()
