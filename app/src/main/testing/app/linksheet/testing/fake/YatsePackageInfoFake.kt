package app.linksheet.testing.fake

import android.os.PatternMatcher
import app.linksheet.testing.util.addDataPaths
import app.linksheet.testing.util.addDataTypes
import app.linksheet.testing.util.addHosts
import app.linksheet.testing.util.buildIntentFilter
import app.linksheet.testing.util.buildPackageInfoTestFake

private val mimeTypeIntentFilter = buildIntentFilter {
    addAction("android.intent.action.VIEW")
    addCategory("android.intent.category.DEFAULT")
    addCategory("android.intent.category.BROWSABLE")
    addDataScheme("http")
    addDataScheme("https")
    addDataTypes(
        "image/*", "video/*", "audio/*", "*/avi", "*/mkv",
        "application/mp4", "application/mp3", "application/mpeg*", "application/ogg",
        "application/vnd.rn-realmedia*", "application/3gpp*", "application/vnd.3gp*",
        "application/vnd.dvd*", "application/vnd.dolby*", "application/x-mpegURL",
        "application/vnd.apple.mpegurl", "application/x-quicktimeplayer"
    )
}

private val hostsIntentFilter = buildIntentFilter {
    addAction("android.intent.action.VIEW")
    addCategory("android.intent.category.DEFAULT")
    addCategory("android.intent.category.BROWSABLE")
    addDataScheme("http")
    addDataScheme("https")
    addHosts(
        "youtube.com", "youtu.be", "*.youtu.be", "*.youtube.com", "www.vimeo.com", "vimeo.com",
        "*.vimeo.com", "www.twitch.tv", "twitch.tv", "*.twitch.tv", "www.dailymotion.com", "dailymotion.com",
        "*.dailymotion.com", "www.myvideo.de", "myvideo.de", "*.myvideo.de", "soundcloud.com", "*.soundcloud.com",
        "drive.google.com", "docs.google.com", "ted.com", "*.ted.com", "svtplay.se", "*.svtplay.se", "bandcamp.com",
        "*.bandcamp.com"
    )
}

private val extensionIntentFilter = buildIntentFilter {
    addAction("android.intent.action.VIEW")
    addCategory("android.intent.category.DEFAULT")
    addCategory("android.intent.category.BROWSABLE")
    addDataScheme("http")
    addDataScheme("https")
    addDataAuthority("*", null)
    addDataPaths(
        PatternMatcher.PATTERN_SIMPLE_GLOB, ".*.avi",
        ".*.asf", ".*.divx", ".*.f4v", ".*.flv", ".*.m2v", ".*.m2ts", ".*.m3u", ".*.m3u8", ".*.mkv", ".*.mp3", ".*.m4v",
        ".*.mp4", ".*.mpeg", ".*.mpg", ".*.mov", ".*.mts", ".*.ogg", ".*.rm", ".*.rmvb", ".*.ts", ".*.vob", ".*.webm",
        ".*.wmv", ".*.wtv", ".*avi", ".*asf", ".*divx", ".*f4v", ".*flv", ".*m2v", ".*m2ts", ".*m3u", ".*m3u8", ".*mkv",
        ".*mp3", ".*mp4", ".*m4v", ".*mpeg", ".*mpg", ".*mov", ".*ogg", ".*oga", ".*mts", ".*rm", ".*rmvb", ".*ts",
        ".*vob", ".*webm", ".*wmv", ".*wtv", ".*.AVI", ".*.ASF", ".*.DIVX", ".*.F4V", ".*.FLV", ".*.M2V", ".*.M2TS",
        ".*.M3U", ".*.M3U8", ".*.MKV", ".*.MP3", ".*.MP4", ".*.M4V", ".*.MPEG", ".*.MPG", ".*.MOV", ".*.MTS", ".*.OGG",
        ".*.OGA", ".*.RM", ".*.RMVB", ".*.TS", ".*.VOB", ".*.WEBM", ".*.WMV", ".*.WTV", ".*AVI", ".*ASF", ".*DIVX",
        ".*F4V", ".*FLV", ".*M2V", ".*M2TS", ".*M3U", ".*M3U8", ".*MKV", ".*MP3", ".*MP4", ".*M4V", ".*MPEG", ".*MPG",
        ".*MOV", ".*MTS", ".*OGG", ".*OGA", ".*RM", ".*RMVB", ".*TS", ".*VOB", ".*WEBM", ".*WMV", ".*WTV"
    )
}

val YatsePackageInfoFake = buildPackageInfoTestFake("org.leetzone.android.yatsewidgetfree", "Yatse") {
    activity("org.leetzone.android.yatsewidget.ui.activity.SendToActivity") {
        addFilter {
            addAction("android.intent.action.VIEW")
            addCategory("android.intent.category.DEFAULT")
            addDataTypes("image/*", "video/*", "audio/*")
        }

        addFilter(mimeTypeIntentFilter)
        addFilter(extensionIntentFilter)
        addFilter(hostsIntentFilter)
    }

    activity("org.leetzone.android.yatsewidgetfree.QueueToActivity") {
        targetActivity = "org.leetzone.android.yatsewidget.ui.activity.SendToActivity"
        addFilter(mimeTypeIntentFilter)
        addFilter(extensionIntentFilter)
        addFilter(hostsIntentFilter)
    }
}
