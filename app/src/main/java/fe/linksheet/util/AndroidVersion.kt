package fe.linksheet.util

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast


object AndroidVersion {
    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    inline val AT_LEAST_API_26_O: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    inline val AT_LEAST_API_28_P
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    inline val AT_LEAST_API_29_Q
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    inline val AT_LEAST_API_30_R
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    inline val AT_LEAST_API_31_S
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    inline val AT_LEAST_API_33_T
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    inline val AT_LEAST_API_34_U
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
    inline val AT_LEAST_API_35_V
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM

    @ChecksSdkIntAtLeast(parameter = 0, lambda = 1)
    inline fun <T> atLeastApi(value: Int, block: () -> T): T? {
        return if (Build.VERSION.SDK_INT >= value) {
            block()
        } else null
    }
}
