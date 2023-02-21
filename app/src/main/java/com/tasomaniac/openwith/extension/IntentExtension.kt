package com.tasomaniac.openwith.extension

import android.content.Intent

fun Intent.isHttp() = "http" == scheme || "https" == scheme
