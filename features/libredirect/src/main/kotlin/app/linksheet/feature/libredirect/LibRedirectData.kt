package app.linksheet.feature.libredirect

import fe.gson.extension.json
import fe.libredirectkt.LibRedirectLoader

object LibRedirectData {
    val RedditService by lazy {
        LibRedirectLoader.createService("reddit", """
        {
          "frontends": {
            "libreddit": {
              "name": "Libreddit",
              "instanceList": true,
              "url": "https://github.com/spikecodes/libreddit",
              "localhost": true
            },
            "redlib": {
              "name": "Redlib",
              "instanceList": true,
              "url": "https://github.com/redlib-org/redlib",
              "localhost": false
            },
            "teddit": {
              "name": "Teddit",
              "instanceList": true,
              "url": "https://codeberg.org/teddit/teddit",
              "localhost": false
            },
            "eddrit": {
              "name": "Eddrit",
              "instanceList": true,
              "url": "https://github.com/corenting/eddrit",
              "localhost": false
            },
            "troddit": {
              "name": "Troddit",
              "instanceList": false,
              "url": "https://github.com/burhan-syed/troddit",
              "localhost": false
            }
          },
          "targets": [
            "^https?:\\/{2}(www\\.|old\\.|np\\.|new\\.|amp\\.)?(reddit|reddittorjg6rue252oqsxryoxengawnmo46qy4kyii5wtqnwfj4ooad)\\.(com|onion)(?\u003d\\/u(ser)?\\/|\\/r\\/|\\/search|\\/new|\\/comments|\\/?$)",
            "^https?:\\/{2}((i|(external-)?preview)\\.)?redd\\.it"
          ],
          "name": "Reddit",
          "options": {
            "enabled": false,
            "frontend": "redlib",
            "unsupportedUrls": "bypass",
            "instance": "public",
            "redirectOnlyInIncognito": false
          },
          "imageType": "svg",
          "url": "https://reddit.com"
        }""".json.asJsonObject)
    }

    val TwitterService by lazy {
        LibRedirectLoader.createService("twitter", """
        {
          "frontends": {
            "nitter": {
              "name": "Nitter",
              "embeddable": true,
              "instanceList": true,
              "url": "https://github.com/zedeus/nitter",
              "localhost": true
            }
          },
          "targets": [
            "^https?:\\/{2}(www\\.|mobile\\.)?twitter\\.com\\/",
            "^https?:\\/{2}(www\\.|mobile\\.)?x\\.com\\/",
            "^https?:\\/{2}(pbs\\.|video\\.)twimg\\.com\\/",
            "^https?:\\/{2}platform\\.x\\.com/embed\\/",
            "^https?:\\/{2}platform\\.twitter\\.com/embed\\/",
            "^https?:\\/{2}t\\.co\\/"
          ],
          "name": "Twitter",
          "options": {
            "enabled": false,
            "redirectType": "main_frame",
            "unsupportedUrls": "bypass",
            "frontend": "nitter",
            "instance": "public",
            "redirectOnlyInIncognito": false
          },
          "imageType": "svg",
          "embeddable": true,
          "url": "https://twitter.com"
        }""".json.asJsonObject)
    }
    val InstagramService by lazy {
        LibRedirectLoader.createService("instagram", """
        {
          "frontends": {
            "proxigram": {
              "name": "Proxigram",
              "instanceList": true,
              "url": "https://codeberg.org/ThePenguinDev/Proxigram",
              "localhost": false
            }
          },
          "targets": [
            "^https?:\\/{2}(www\\.)?instagram\\.com"
          ],
          "name": "Instagram",
          "options": {
            "enabled": false,
            "frontend": "proxigram",
            "unsupportedUrls": "bypass",
            "instance": "public",
            "redirectOnlyInIncognito": false
          },
          "imageType": "svg",
          "url": "https://www.instagram.com"
        }
        """.json.asJsonObject)
    }
    val GithubService by lazy {
        LibRedirectLoader.createService("github", """
        {
          "frontends": {
            "gothub": {
              "name": "Gothub",
              "instanceList": true,
              "url": "https://codeberg.org/gothub/gothub"
            }
          },
          "targets": [
            "^https?:\\/{2}github\\.com\\/",
            "^https?:\\/{2}gist\\.github\\.com\\/[^\\/]+\\/[^\\/]+\\/?",
            "^https?:\\/{2}raw\\.githubusercontent\\.com\\/[^\\/]+\\/[^\\/]+\\/?"
          ],
          "name": "GitHub",
          "options": {
            "enabled": false,
            "unsupportedUrls": "bypass",
            "frontend": "gothub",
            "redirectOnlyInIncognito": false
          },
          "imageType": "svgMono",
          "url": "https://github.com"
        }
        """.json.asJsonObject)
    }

    val RedLibInstance by lazy {
        LibRedirectLoader.createInstance("redlib", """
        {
          "clearnet": [
            "https://l.opnxng.com",
            "https://redlib.catsarch.com",
            "https://redlib.perennialte.ch",
            "https://redlib.nohost.network",
            "https://redlib.ducks.party",
            "https://red.artemislena.eu",
            "https://redlib.privacyredirect.com",
            "https://redlib.4o1x5.dev",
            "https://redlib.frontendfriendly.xyz",
            "https://redlib.reallyaweso.me",
            "https://reddit.adminforge.de",
            "https://lr.ptr.moe",
            "https://redlib.orangenet.cc",
            "https://redlib.privadency.com"
          ],
          "tor": [
            "http://red.lpoaj7z2zkajuhgnlltpeqh3zyq7wk2iyeggqaduhgxhyajtdt2j7wad.onion"
          ],
          "i2p": [],
          "loki": []
        }""".json.asJsonObject)
    }

    val NitterInstance by lazy {
        LibRedirectLoader.createInstance("nitter", """
        {
        "clearnet": [
          "https://xcancel.com",
          "https://nitter.poast.org",
          "https://nitter.privacyredirect.com",
          "https://lightbrd.com",
          "https://nitter.space",
          "https://nitter.tiekoetter.com",
          "https://nuku.trabun.org",
          "https://nitter.kuuro.net",
          "https://worldcorrespondents.com"
        ],
        "tor": [
          "http://nitter.coffee2m3bjsrrqqycx6ghkxrnejl2q6nl7pjw2j4clchjj6uk5zozad.onion",
          "http://nitter.kuuro5abqix6tfku77wj32srkicgqh3f7ro77ctpda7ub7a7mlv4jsid.onion"
        ],
        "i2p": [],
        "loki": []
      }""".json.asJsonObject)
    }
    val GothubInstance by lazy {
        LibRedirectLoader.createInstance("gothub", """
        {
          "clearnet": [
            "https://gothub.lunar.icu",
            "https://g.opnxng.com",
            "https://gh.owo.si",
            "https://gothub.projectsegfau.lt",
            "https://gothub.r4fo.com",
            "https://gothub.dev.projectsegfau.lt",
            "https://gh.phreedom.club",
            "https://gothub.ducks.party"
          ],
          "tor": [],
          "i2p": [],
          "loki": []
        }
        """.json.asJsonObject)
    }
}
