package fe.clearurlkt

import assertk.Table2
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.clearurlskt.ClearUrls
import fe.clearurlskt.loader.BundledClearURLConfigLoader
import fe.std.test.trimMargin
import kotlin.test.Test

internal class UrlClearTest {

    private fun String.trimTestMargin(): String {
        return trimMargin(lineSeparator = "")
    }

    private val tests = tableOf("input", "expected")
        .row("deezer.com/track/891177062?utm_source=deezer", "https://deezer.com/track/891177062")
        .row("https://deezer.com/track/891177062?utm_source=deezer", "https://deezer.com/track/891177062")
        .row("https://DEEZER.com/track/891177062?utm_source=deezer", "https://DEEZER.com/track/891177062")
        .row("HTTPS://DEEZER.com/TRACK/891177062?UTM_SOURCE=deezer", "HTTPS://DEEZER.com/TRACK/891177062")
        .row("https://www.google.com/url?q=https://pypi.org/project/Unalix", "https://pypi.org/project/Unalix")
        .row("https://www.amazon.com/gp/B08CH7RHDP/ref=as_li_ss_tl", "https://www.amazon.com/gp/B08CH7RHDP")
        .row("http://0.0.0.0/?utm_source=local", "http://0.0.0.0/")
        .row("https://myaccount.google.com/?utm_source=google", "https://myaccount.google.com/?utm_source=google")
        .row("http://example.com/?&&&&", "http://example.com/?&&&&")
        .row("http://example.com/?p1=&p2=", "http://example.com/?p1=&p2=")
        .row("http://example.com/?p1=value&p1=othervalue", "http://example.com/?p1=value&p1=othervalue")
        .row("https://example.com/##", "https://example.com/#%23")
        .row("https://example.com/??", "https://example.com/??")
        .row("https://example.com/#xxxxxxxxxx#", "https://example.com/#xxxxxxxxxx%23")
        .row("https://l.instagram.com/?u=https://linksheet.app/&e=tracking-token", "https://linksheet.app/")
        .row("https://l.instagram.com/?u=linksheet.app/&e=tracking-token", "https://linksheet.app/")
        .row("https://www.google.com/search?q=%E4%B8%AD%E6%96%87", "https://www.google.com/search?q=%E4%B8%AD%E6%96%87")
        .row(
            "https://www.google.com/search?q=never%20gonna%20give%20you%20up",
            "https://www.google.com/search?q=never%20gonna%20give%20you%20up"
        )
        .row(
            "https://www.google.com/search?q=never+gonna+give+you+up",
            "https://www.google.com/search?q=never%20gonna%20give%20you%20up"
        )
        .row(
            "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings",
            "https://docs.julialang.org/en/v1/stdlib/REPL/#Key-bindings"
        )
        .row(
            "https://twitter.com/DelusionPosting/status/1630991327381929987?t=AP1I12BA7jOlee95KLpgqX&s=19",
            "https://twitter.com/DelusionPosting/status/1630991327381929987"
        )
        .row(
            "https://open.spotify.com/playlist/spottifei?si=lol&pt=trash_app",
            "https://open.spotify.com/playlist/spottifei?pt=trash_app"
        )
        .row(
            "https://www.google.com/amp/s/de.statista.com/infografik/amp/22496/anzahl-der-gesamten-positiven-corona-tests-und-positivenrate/",
            "https://de.statista.com/infografik/amp/22496/anzahl-der-gesamten-positiven-corona-tests-und-positivenrate/"
        )
        .row(
            "https://myanimelist.net/v1/oauth2/authorize?client_id=xd&code_challenge=lule123&response_type=code",
            "https://myanimelist.net/v1/oauth2/authorize?client_id=xd&code_challenge=lule123&response_type=code"
        )
        .row(
            """
            |https://m.facebook.com/v16.0/dialog/oauth?cct_prefetching=0&client_id=cringe&cbt=lol&e2e=xd&ies=0
            |&sdk=android-16.1.3&sso=chrome_custom_tab&nonce=jabro&scope=openid%2Cpublic_profile%2Cemail
            |&state=Lule&code_challenge_method=S256&default_audience=friends&login_behavior=NATIVE_WITH_FALLBACK
            |&redirect_uri=fbconnect%3A%2F%2Fnö&auth_type=rerequest&response_type=id_token%2Ctoken%2Csigned_request%2Cgraph_domain
            |&return_scopes=true&code_challenge=facebook_sucks
            |""".trimTestMargin(),
            """
            |https://m.facebook.com/v16.0/dialog/oauth?cct_prefetching=0&client_id=cringe&cbt=lol&e2e=xd&ies=0&sdk=android-16.1.3
            |&sso=chrome_custom_tab&nonce=jabro&scope=openid,public_profile,email&state=Lule&code_challenge_method=S256
            |&default_audience=friends&login_behavior=NATIVE_WITH_FALLBACK&redirect_uri=fbconnect://n%EF%BF%BD&auth_type=rerequest
            |&response_type=id_token,token,signed_request,graph_domain&return_scopes=true&code_challenge=facebook_sucks
            |""".trimTestMargin()
        )
        .row(
            """
            |https://lm.facebook.com/l.php?u=
            |https%3A%2F%2Fbit.ly%2F3tTxAv4%3Ffbclid%3DIwAR2BRY7IuBvxCV8OI74v-lWKb0RZAHEmVjfGn2OCRLYJpdrfz2Ow47UqLJc
            |&h=AT1vCA39uUU-mV4NAf7NyueUILrXGPNjF4c1I_YVs6rdBcifbHQI5pVII5W2X4C1ORr01CKJf4VcTV4Mg9xMuz63vj6F
            |-KHHB3OMDASwnc9lEAM8OlU51rVsJNWB_gUhj3K1s401VC4l4_h3E_5R
            |""".trimTestMargin(),
            "https://bit.ly/3tTxAv4"
        )
        .row(
            """
            |https://social.tchncs.de/oauth/authorize?client_id=CLIENT_LEL
            |&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=read
            |""".trimTestMargin(),
            """
            |https://social.tchncs.de/oauth/authorize?client_id=CLIENT_LEL
            |&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=read
            |""".trimTestMargin(),
        )
        .row(
            """
            |https://www.google.com/url?q=https://minecrafthelp.zendesk.com/requests/9999999999999999999/
            |satisfaction/new/asfdasfasfasfasdasdasdasdasd?locale%3D1%26intention%3D16&source=gmail
            |&ust=999999999999999999999&usg=asdafsasfasfasfasfaf
            |""".trimTestMargin(),
            """
            |https://minecrafthelp.zendesk.com/requests/9999999999999999999/satisfaction/new/
            |asfdasfasfasfasdasdasdasdasd?locale=1&intention=16
            |""".trimTestMargin(),
        )
        .row(
            """
            |https://ingka.page.link/?link=https://order.ikea.com/at/history/%23/lookup?orderId%3Dikea123%26lid%3Dhello
            |&apn=com.ingka.ikea.app&afl=https://order.ikea.com/at/de/purchases/ikea_ikea/?lid=hello&ibi=com.ingka.ikea.app
            |&ifl=https://order.ikea.com/at/de/purchases/ikea_ikea/?lid=hello
            |&ofl=https://order.ikea.com/at/de/purchases/ikea_ikea/?lid=hello&imv=1.2.3&amv=1100
            |""".trimTestMargin(),
            """
            |https://ingka.page.link/?link=https://order.ikea.com/at/history/%23/lookup?orderId=ikea123&lid=hello
            |&apn=com.ingka.ikea.app&afl=https://order.ikea.com/at/de/purchases/ikea_ikea/?lid=hello&ibi=com.ingka.ikea.app
            |&ifl=https://order.ikea.com/at/de/purchases/ikea_ikea/?lid=hello
            |&ofl=https://order.ikea.com/at/de/purchases/ikea_ikea/?lid=hello&imv=1.2.3&amv=1100
            |""".trimTestMargin(),
        )
        .row(
            """
            |https://analyticsindiamag.com/iit-bombay-joins-ibm-meta-to-form-ai-alliance-challenging-openai-google-amazon-microsoft/
            |?utm_source=rss&utm_medium=rss
            |&utm_campaign=iit-bombay-joins-ibm-meta-to-form-ai-alliance-challenging-openai-google-amazon-microsoft
            |""".trimTestMargin(),
            """
            |https://analyticsindiamag.com/iit-bombay-joins-ibm-meta-to-form-ai-alliance-challenging-openai-google-amazon-microsoft/
            |""".trimTestMargin(),
        )
        .row(
            """
            |https://accounts.nintendo.com/connect/1.0.0/authorize?state=aGb6tyEy1VlsoOh96xsttpK2jg7FlqM0EmI5ovlSDrOEDIkSz7
            |&redirect_uri=npfd123dc2adf715a15%3A%2F%2Fauth&client_id=d123dc2adf715a15&lang=en-US
            |&scope=openid+user+user.birthday+user%3AanyUsers%3Apublic+mission+missionStatus+missionCompletion+members
            |%3Aauthenticate+userGift%3Areceive+pointWallet+rewardStatus+rewardExchange%3Acreate&response_type=session_token_code
            |&session_token_code_challenge=SPD1LQJgsnzwrjn54g3DYZZ96sEWAYmLVhEUpLDoUw7&session_token_code_challenge_method=S256
            |""".trimTestMargin(),
            """
            |https://accounts.nintendo.com/connect/1.0.0/authorize?state=aGb6tyEy1VlsoOh96xsttpK2jg7FlqM0EmI5ovlSDrOEDIkSz7
            |&redirect_uri=npfd123dc2adf715a15://auth&client_id=d123dc2adf715a15&lang=en-US
            |&scope=openid%20user%20user.birthday%20user:anyUsers:public%20mission%20missionStatus%20missionCompletion
            |%20members:authenticate%20userGift:receive%20pointWallet%20rewardStatus%20rewardExchange:create
            |&response_type=session_token_code&session_token_code_challenge=SPD1LQJgsnzwrjn54g3DYZZ96sEWAYmLVhEUpLDoUw7
            |&session_token_code_challenge_method=S256
            |""".trimTestMargin(),
        )
        .row(
            """
            |https://store.epicgames.com/purchase
            |?offers=1-e52b4d8cbcfd45ad95eff800ccc59d93-54003c89a604467abe315658a4a853b3
            |&offers=1-99d8ac4b0cd94611b52bae792d84b0e4-5e416c7c1444483e828899b0c30ce2cd
            |""".trimTestMargin(),
            """
            |https://store.epicgames.com/purchase
            |?offers=1-e52b4d8cbcfd45ad95eff800ccc59d93-54003c89a604467abe315658a4a853b3
            |&offers=1-99d8ac4b0cd94611b52bae792d84b0e4-5e416c7c1444483e828899b0c30ce2cd
            |""".trimTestMargin()
        )
        .row(
            """https://cryptpad.fr/doc/
            |#/2/doc/view/JHBAaIWBxeAJxjCkRsfc9teDgekmZRTy3NpYr5jIioM/embed/
            |""".trimTestMargin(),
            """https://cryptpad.fr/doc/
            |#/2/doc/view/JHBAaIWBxeAJxjCkRsfc9teDgekmZRTy3NpYr5jIioM/embed/
            |""".trimTestMargin()
        )
        .row(
            """https://sso.willhaben.at/auth/realms/willhaben/protocol/openid-connect/auth
            |?response_type=code
            |&client_id=CLIENT1
            |&scope=openid
            |&state=STATE_ABC1
            |&redirect_uri=https://www.willhaben.at/webapi/oauth2/code/sso&nonce=NONCE_ABC1
            |""".trimTestMargin(),
            """https://sso.willhaben.at/auth/realms/willhaben/protocol/openid-connect/auth
            |?response_type=code
            |&client_id=CLIENT1
            |&scope=openid
            |&state=STATE_ABC1
            |&redirect_uri=https://www.willhaben.at/webapi/oauth2/code/sso&nonce=NONCE_ABC1
            |""".trimTestMargin(),
        )


    private val providers = BundledClearURLConfigLoader.load().getOrNull()!!
    private val clearUrl = ClearUrls(providers)

    private fun runTest(input: String): String {
        val (result, operations) = clearUrl.clearUrl(input)

        println("ClearUrl($input): $result")
        for (operation in operations) {
            println("\t$operation")
        }

        println()
        return result
    }

    private fun Table2<String, String>.testAll() {
        forAll { input, expected ->
            assertThat(runTest(input)).isEqualTo(expected)
        }
    }

    @Test
    fun test() {
        tests.testAll()
    }
}
