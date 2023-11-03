package fe.linksheet

import androidx.annotation.StringRes

const val openLinkWithGithub = "https://github.com/tasomaniac/OpenLinkWith"
const val mastodonRedirectGithub = "https://github.com/zacharee/MastodonRedirect"
const val linksheetGithub = "https://github.com/1fexd/LinkSheet"
const val workflowIdPath = "actions/runs"

const val discordInvite = "https://discord.gg/GVNphPrQPf"

const val donationBuyMeACoffee = "https://www.buymeacoffee.com/1fexd"
const val donationCrypto = "https://coindrop.to/fexd"
const val linkSheetCompatGithubReleases = "https://github.com/1fexd/LinkSheetCompat/releases"

const val shizukuDownload = "https://shizuku.rikka.app/download"

const val supabaseFunctionHost = "https://rhxxkicztujvnyntzmmb.functions.supabase.co"
const val supabaseApiKey =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJoeHhraWN6dHVqdm55bnR6bW1iIiwicm9sZSI6ImFub24iLCJpYXQiOjE2Nzc5MjkxMzAsImV4cCI6MTk5MzUwNTEzMH0.KM5GBWk3DzZu5Zx4gcCbIwNPKnPZT8Qi-7_JKwjimsE"

const val donationBannerAfterMinutes = 10
const val developmentTimeHours = 250
const val developmentTimeMonths = 6

enum class BuildType(@StringRes val stringRes: Int) {
    Manual(R.string.manual_build), GithubPipeline(R.string.github_pipeline_build)
}

val officialSigningKeys = mapOf(
    "C2A8B18C328DFB39C896491757ED11C145D3ACCA43212FA3DE362433C416AAA9" to BuildType.Manual,
    "3FCF7675BC90E239892C7262499DCC9F8CE6A52B7E58D02B56AA60CA669D6701" to BuildType.GithubPipeline
)

val lineSeparator: String = System.getProperty("line.separator") ?: "\n"