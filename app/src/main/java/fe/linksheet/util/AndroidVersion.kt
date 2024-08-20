package fe.linksheet.util

import android.os.Build


object AndroidVersion {
    inline val API_26_0 get() = Build.VERSION_CODES.O
    inline val API_28_P get() = Build.VERSION_CODES.P
    inline val API_29_Q get() = Build.VERSION_CODES.Q
    inline val API_30_R get() = Build.VERSION_CODES.R
    inline val API_31_S get() = Build.VERSION_CODES.S
    inline val API_33_T get() = Build.VERSION_CODES.TIRAMISU
    inline val API_34_U get() = Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    inline val AT_LEAST_API_26_O get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    inline val AT_LEAST_API_28_P get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    inline val AT_LEAST_API_29_Q get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    inline val AT_LEAST_API_30_R get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    inline val AT_LEAST_API_31_S get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    inline val AT_LEAST_API_33_T get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    inline val AT_LEAST_API_34_U get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    inline fun <T> atLeastApi30R(block: () -> T): T? {
        return if (AT_LEAST_API_30_R) block() else null
    }

    inline fun <T> atLeastApi31S(block: () -> T): T? {
        return if (AT_LEAST_API_31_S) block() else null
    }
}
