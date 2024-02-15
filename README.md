<!-- ---------- Mascot ---------- -->
<div align="">
  <h1>LinkSheet</h1>
 </div>



<!-- ---------- Description ---------- -->
<div align="">

LinkSheet restores the Url-App-Chooser that was removed in Android 12+ in favor
of [verified app links](https://developer.android.com/training/app-links/verify-android-applinks).

</div>


<!-- ---------- Badges ---------- -->
<div align="">

New "official" releases are rarely built, but the app is still rapidly being developed, therefore we recommend using
the [nightly builds](#nightly-builds).

[![GitHub version](https://img.shields.io/github/v/release/1fexd/LinkSheet)](https://github.com/1fexd/LinkSheet/releases/latest)
[![IzzyOnDroid](https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/fe.linksheet)](https://apt.izzysoft.de/fdroid/index/apk/fe.linksheet)

[![Latest Github nightly release](https://img.shields.io/github/v/release/1fexd/LinkSheetNightly?label=nightly%20github&color=orange)](https://github.com/1fexd/LinkSheetNightly/releases/latest)
[![Nightly (via nightly.link)](https://img.shields.io/badge/nightly-latest-orange?label=nightly.link&color=orange)](https://nightly.link/1fexd/LinkSheet/workflows/build-nightly/master/linksheet-nightly.zip)

[![Build status](https://img.shields.io/github/actions/workflow/status/1fexd/LinkSheet/build-nightly.yml)](https://github.com/1fexd/LinkSheet/actions/workflows/build-nightly.yml)
[![Downloads](https://img.shields.io/github/downloads/1fexd/LinkSheet/total)](https://github.com/1fexd/LinkSheet/releases)
[![Downloads](https://img.shields.io/github/downloads/1fexd/LinkSheetNightly/total?color=orange)](https://github.com/1fexd/LinkSheetNightly/releases)
[![Translations](https://img.shields.io/weblate/progress/linksheet)](https://hosted.weblate.org/projects/linksheet/)

</div>

<!-- ---------- Download ---------- -->
<div align="">

[<img src="readme/IzzyOnDroid.png"
alt="Get it on IzzySoft"
height="80">](https://apt.izzysoft.de/fdroid/index/apk/fe.linksheet)
</div>




<!-- ---------- Screenshots ---------- -->
<div align="">
  <div style="display: flex;">
    <img src="readme/screenshots.webp">
  </div>
 </div>

## Donations

<div align="">
    <a href="https://www.buymeacoffee.com/1fexd" target="_blank"><img
            src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png"
            alt="Buy Me A Coffee"
            style="border-radius: 10px; height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" /></a>
    <a href="https://coindrop.to/fexd" target="_blank">
        <img src="https://coindrop.to/embed-button.png" alt="Coindrop.to me" style="border-radius: 10px; !important; height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" />
    </a>
</div>

## Join our Discord

<div align="">
  <a href="https://discord.gg/XndZet2pWF"><img src="https://discordapp.com/api/guilds/1137845851344081038/widget.png?style=banner2" alt="Discord Banner 2"/></a>
</div>

## Features

* Set preferred browser: Always list all browsers, only list native apps, always list selected browser, only list
  whitelisted browsers
* Preferred app for host: Set app to always open for selected host
* List apps which have verified links so you can easily disable them to allow LinkSheet to work
* Include usage stats and "last app used history" in sorting algorithm
* Copy url button in bottom sheet which copies the current url to the clipboard
* Share To button in bottom sheet which sends a "Share To" intent to other apps
* Single tap: Only require a single tap in bottom sheet to open app
* Always show package name: Shows package names all throughout the application
* Disable toast: Disables all toast messages
* Display apps in grid instead of list
* *Experimental:* [ClearURLs](https://github.com/ClearURLs) integration (removes tracking parameters) (report
  issues/bugs [here](https://github.com/1fexd/clearurlkt))
* *Experimental:* [FastFoward](https://github.com/FastForwardTeam/FastForward) **rule** integration (extracts redirects
  from url parameters) (report issues/bugs [here](https://github.com/1fexd/fastforwardkt))
* *Experimental:* [LibRedirect](https://github.com/libredirect/libredirect) integration (redirects to non-proprietary
  frontends) (report issues/bugs [here](https://github.com/1fexd/libredirectkt))

<ul>
  <li><strong>Pro:</strong> Resolve redirects either locally or via API hosted @ supabase.com (<a href="https://supabase.com/privacy">Supabase Privacy policy</a>), API itself does not log anything except timestamp</li>
  <li><strong>Pro:</strong> Priority support - your feature requests and/or bug fixes will be prioritized</li>
  <li><strong>Pro:</strong> Sustain LinkSheet's development - a small donation keeps me motivated to work on the app - thank you!</li>
</ul>

## Donations / LinkSheet Pro

All donations of

* 4€+ (Crypto) or
* 5€+ (all other means of payment)

are eligible for ✨ LinkSheet Pro ✨ - donate via the links above

## Nightly builds

Github automatically builds a "nightly" version of LinkSheet on push (e.g. meaning one or more commits have occurred).
The latest nightly version can always be downloaded
via [nightly.link](https://nightly.link/1fexd/LinkSheet/workflows/build-nightly/master/linksheet-nightly.zip), but if
you are using something like [Obtainium](https://github.com/ImranR98/Obtainium), there also is
a [LinkSheetNightly](https://github.com/1fexd/LinkSheetNightly) repository where nightly builds are released as Github
releases.

## License

* LinkSheet, excluding all `strings.xml` and `plural.xml` files, is licensed under the [FXL](LICENSE)
* All `strings.xml` and `plural.xml` files are licensed under [GPL-3.0](LICENSE_STRINGS)

## Translations

* First of all, thank you to everyone who has contributed translations to this project
  via [Weblate](https://hosted.weblate.org/projects/linksheet/)
* Secondly, please acknowledge that all translations (e.g. everything that is not english) is maintained by the
  community. I do not check these translations currently, so if there are mistakes or intentional errors please open an
  issue or correct them yourself!
* Thirdly, special shout-out to [@Daviteusz](https://github.com/Daviteusz) for providing translation updates incredibly
  fast.

## Acknowledgements

* App was initially based on [OpenLinkWith](https://github.com/tasomaniac/OpenLinkWith)
* [Seal](https://github.com/JunkFood02/Seal) has been a reference to learn from

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=1fexd/LinkSheet&type=Date)](https://star-history.com/#1fexd/LinkSheet&Date)
