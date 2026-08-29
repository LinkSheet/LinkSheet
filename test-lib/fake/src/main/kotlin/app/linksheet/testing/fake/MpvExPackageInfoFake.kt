package app.linksheet.testing.fake

import android.os.PatternMatcher
import app.linksheet.testing.util.addDataPaths
import app.linksheet.testing.util.addDataTypes
import app.linksheet.testing.util.addHosts
import app.linksheet.testing.util.buildPackageInfoTestFakeLazy

object MpvExPackageInfoFake {
    const val PlayerActivity = "app.marlboroadvance.mpvex.ui.player.PlayerActivity"
    val PackageInfo by buildPackageInfoTestFakeLazy("app.marlboroadvance.mpvex", "mpvEx") {
        activity(PlayerActivity) {
            addFilter {
                addAction("android.intent.action.VIEW")
                addCategory("android.intent.category.DEFAULT")
                addCategory("android.intent.category.BROWSABLE")
                addDataScheme("content")
                addDataScheme("file")
                addDataScheme("http")
                addDataScheme("https")
                addDataTypes(
                    "video/*",
                    "audio/*",
                    "*/rmvb",
                    "*/avi",
                    "*/mkv",
                    "application/3gpp*",
                    "application/mp4",
                    "application/mpeg*",
                    "application/ogg",
                    "application/sdp",
                    "application/vnd.3gp*",
                    "application/vnd.apple.mpegurl",
                    "application/vnd.dvd*",
                    "application/vnd.dolby*",
                    "application/vnd.rn-realmedia*",
                    "application/x-extension-mp4",
                    "application/x-flac",
                    "application/x-matroska",
                    "application/x-mpegurl",
                    "application/x-ogg",
                    "application/x-quicktimeplayer"
                )
            }
            addFilter {
                addAction("android.intent.action.VIEW")
                addCategory("android.intent.category.DEFAULT")
                addCategory("android.intent.category.BROWSABLE")
                addDataScheme("rtmp")
                addDataScheme("rtmps")
                addDataScheme("rtp")
                addDataScheme("rtsp")
                addDataScheme("mms")
                addDataScheme("mmst")
                addDataScheme("mmsh")
                addDataScheme("tcp")
                addDataScheme("udp")
            }
            addFilter {
                addAction("android.intent.action.SEND")
                addCategory("android.intent.category.DEFAULT")
                addDataTypes("video/*", "audio/*", "image/*", "text/plain")
            }
            addFilter {
                addAction("android.intent.action.VIEW")
                addCategory("android.intent.category.DEFAULT")
                addCategory("android.intent.category.BROWSABLE")
                addDataScheme("http")
                addDataScheme("https")
                addHosts("*")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.mkv")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.mkv")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.mkv")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.mkv")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.mkv")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.mkv"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.mkv"
                )
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.mp4")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.mp4")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.mp4")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.mp4")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.mp4")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.mp4"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.mp4"
                )
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.webm")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.webm")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.webm")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.webm")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.webm")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.webm"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.webm"
                )
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.avi")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.avi")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.avi")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.avi")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.avi")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.avi"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.avi"
                )
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.mov")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.mov")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.mov")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.mov")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.mov")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.mov"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.mov"
                )
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.m4v")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.m4v")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.m4v")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.m4v")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.m4v")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.m4v"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.m4v"
                )
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.flac")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.flac")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.flac")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.flac")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.flac")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.flac"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.flac"
                )
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.mp3")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.mp3")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.mp3")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.mp3")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.mp3")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.mp3"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.mp3"
                )
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.ogg")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.ogg")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.ogg")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.ogg")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.ogg")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.ogg"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.ogg"
                )
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\.m3u8*")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\.m3u8*")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\.m3u8*")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\.m3u8*")
                addDataPaths(PatternMatcher.PATTERN_SIMPLE_GLOB, ".*\\..*\\..*\\..*\\..*\\.m3u8*")
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\.m3u8*"
                )
                addDataPaths(
                    PatternMatcher.PATTERN_SIMPLE_GLOB,
                    ".*\\..*\\..*\\..*\\..*\\..*\\..*\\.m3u8*"
                )
            }
        }
    }
}
