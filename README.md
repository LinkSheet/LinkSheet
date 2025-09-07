# LinkSheet (Diganta743251 Fork) [![Translations](https://img.shields.io/weblate/progress/linksheet)](https://hosted.weblate.org/projects/linksheet/) [![Latest Github nightly release](https://img.shields.io/github/v/release/LinkSheet/nightly?label=download&color=orange)](https://github.com/LinkSheet/nightly/releases/latest) ![Discord](https://img.shields.io/discord/1137845851344081038?label=discord)



**LinkSheet** reimplements the Pre-Android 12 system link handling behavior in a standalone app, allowing users to choose which app to open links in.

This is a fork of the original LinkSheet repository, customized by Diganta743251 for personal development and enhancements.

> [!IMPORTANT]
> LinkSheet is rapidly evolving and has not had a "stable" release in over a year - please use the [nightly builds](#nightly-builds) for the time being

<!-- ---------- Badges ---------- -->
<div align="center">

[![GitHub version](https://img.shields.io/github/v/release/Diganta743251/LinkSheet)](https://github.com/Diganta743251/LinkSheet/releases/latest)

<!--
~~[![IzzyOnDroid](https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/fe.linksheet)](https://apt.izzysoft.de/fdroid/index/apk/fe.linksheet)~~
-->

<!--
// [![Nightly (via nightly.link)](https://img.shields.io/badge/nightly-latest-orange?label=nightly.link&color=orange)](https://nightly.link/Diganta743251/LinkSheet/workflows/build-nightly/master/linksheet-nightly.zip)
-->


</div>

<!-- ---------- Download ---------- -->
<div align="center">
<!--  
  <a href="https://apt.izzysoft.de/fdroid/index/apk/fe.linksheet" target="_blank"><img
            src="readme/IzzyOnDroid.png"
            alt="Get it on IzzySoft" height="80" /></a>
-->
  
   <a href="https://apps.obtainium.imranr.dev/redirect.html?r=obtainium://app%2F%7B%22id%22%3A%22fe.linksheet.nightly%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FLinkSheet%2Fnightly%22%2C%22author%22%3A%221fexd%22%2C%22name%22%3A%22LinkSheet%20Nightly%22%2C%22preferredApkIndex%22%3A0%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Atrue%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22filterReleaseTitlesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22filterReleaseNotesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22verifyLatestTag%5C%22%3Afalse%2C%5C%22dontSortReleasesList%5C%22%3Afalse%2C%5C%22useLatestAssetDateAsReleaseDate%5C%22%3Afalse%2C%5C%22trackOnly%5C%22%3Afalse%2C%5C%22skipUpdateNotifications%5C%22%3Afalse%2C%5C%22about%5C%22%3A%5C%22Restore%20link%20control%20on%20Android%2012%2B%5C%22%7D%22%2C%22shizukuPretendToBeGooglePlay%5C%22%3Afalse%2C%5C%22exemptFromBackgroundUpdates%5C%22%3Afalse%2C%5C%22skipUpdateNotifications%5C%22%3Afalse%2C%5C%22about%5C%22%3A%5C%22%5C%22%2C%5C%22shizukuPretendToBeGooglePlay%5C%22%3Afalse%2C%5C%22exemptFromBackgroundUpdates%5C%22%3Afalse%2C%5C%22skipUpdateNotifications%5C%22%3Afalse%2C%5C%22about%5C%22%3A%5C%22Restore%20link%20control%20on%20Android%2012%2B%5C%22%7D%22%7D" target="_blank"><img
            src="readme/badge_obtainium.png"
            alt="Get it on Obtainium" height="80" /></a>

</div>



<!-- ---------- Screenshots ---------- -->
<div align="center">
  <div style="display: flex;">
    <img src="readme/screenshots.webp">
  </div>
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

## About This Fork

This repository is a fork of the original [LinkSheet](https://github.com/LinkSheet/LinkSheet) project. It includes personal customizations and potential enhancements by Diganta743251. Contributions are welcome, but please note that this fork may diverge from the upstream.

## Nightly builds

Currently, LinkSheet uses a "nightly" "rolling release" model which allows for fast development iteration at the cost of stability; However, since this has proven to work quite well, there usually aren't any issues to be expected.

<!--
* Latest build is always available via [nightly.link](https://nightly.link/Diganta743251/LinkSheet/workflows/build-nightly/master/linksheet-nightly.zip)
-->

* Use [Obtainium](https://github.com/ImranR98/Obtainium) with the [nightly](https://github.com/LinkSheet/nightly) repository to auto-download builds
  
## License

* LinkSheet, excluding all `strings.xml` and `plural.xml` files, is licensed under the [LPL](LICENSE)
* All `strings.xml` and `plural.xml` files are licensed under [GPL-3.0](LICENSE_STRINGS)

## Translations

* Translations are managed via [Weblate](https://hosted.weblate.org/projects/linksheet/)
* All translations except for English are maintained by contributors

## Discord

<div align="center">
  <a href="https://discord.gg/XndZet2pWF"><img src="https://discordapp.com/api/guilds/1137845851344081038/widget.png?style=banner2" alt="Discord Banner 2"/></a>
</div>

## Donations

All donations of 5€+ are eligible for ✨ LinkSheet Pro ✨

<div align="center">
    <a href="https://www.buymeacoffee.com/1fexd" target="_blank"><img
            src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png"
            alt="Buy Me A Coffee"
            style="border-radius: 10px; height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" /></a>
    <a href="https://coindrop.to/fexd" target="_blank">
        <img src="https://coindrop.to/embed-button.png" alt="Coindrop.to me" style="border-radius: 10px; !important; height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" />
    </a>
</div>

## Credits

* LinkSheet was initially based on [OpenLinkWith](https://github.com/tasomaniac/OpenLinkWith)
* [MastodonRedirect](https://github.com/zacharee/MastodonRedirect): Reference for the Shizuku implementation
* Design inspired by [Seal](https://github.com/JunkFood02/Seal) and [GMS-Flags](https://github.com/polodarb/GMS-Flags)

## Contributing

Contributions to this fork are welcome! Please fork the repository, make your changes, and submit a pull request.

## Contact

For questions or issues specific to this fork, contact Diganta743251 via GitHub.
