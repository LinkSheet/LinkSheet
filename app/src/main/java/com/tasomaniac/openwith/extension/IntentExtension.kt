package com.tasomaniac.openwith.extension

import android.content.Intent

fun Intent.isSchemeTypicallySupportedByBrowsers() = "http" == scheme || "https" == scheme
