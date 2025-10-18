package fe.amp2htmlkt

import fe.httpkt.Request
import fe.httpkt.ext.getGZIPOrDefaultStream
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals


class TestMain {
    @Test
    fun test() {
        val request = Request()
        val map = mapOf(
//            "https://www.bbc.com/portuguese/articles/c0jp9l8k8geo.amp" to "https://www.bbc.com/portuguese/articles/c0jp9l8k8geo",
//            "https://historia.nationalgeographic.com.es/a/civilizacion-valle-indo-es-mas-antigua-que-egipcia-y-babilonica_6828/amp" to "https://historia.nationalgeographic.com.es/a/civilizacion-valle-indo-es-mas-antigua-que-egipcia-y-babilonica_6828",
//            "https://google.com" to null,
            "https://amp.cnn.com/cnn/2023/06/19/europe/titanic-shipwreck-vessel-missing-intl/index.html" to "https://www.cnn.com/2023/06/19/europe/titanic-shipwreck-vessel-missing-intl/index.html"
        )

        map.forEach { (ampUrl, expectedCanonical) ->
            val ampUri = URI.create(ampUrl)
            val foundCanonical = request.get(ampUrl).getGZIPOrDefaultStream()?.use {
                Amp2Html.getNonAmpLink(it, ampUri.host)
            }

            assertEquals(expectedCanonical, foundCanonical)
        }
    }
}
