package fe.linksheet

import fe.linksheet.util.AppSignature

private const val github = "https://github.com"
private const val linksheetOrg = "$github/LinkSheet"


const val openLinkWithGithub = "$github/tasomaniac/OpenLinkWith"
const val mastodonRedirectGithub = "$github/zacharee/MastodonRedirect"
const val linksheetGithub = "$linksheetOrg/LinkSheet"

const val linkSheetCompatGithubReleases = "$linksheetOrg/compat/releases"

const val shizukuDownload = "https://shizuku.rikka.app/download"
val officialSigningKeys = mapOf(
    "C2A8B18C328DFB39C896491757ED11C145D3ACCA43212FA3DE362433C416AAA9" to AppSignature.SignatureBuildType.Manual,
    "3FCF7675BC90E239892C7262499DCC9F8CE6A52B7E58D02B56AA60CA669D6701" to AppSignature.SignatureBuildType.GithubPipeline,
)

val lineSeparator: String = System.lineSeparator()
