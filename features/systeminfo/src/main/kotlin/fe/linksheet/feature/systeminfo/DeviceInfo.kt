package fe.linksheet.feature.systeminfo

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BuildInfo(
    @SerializedName("version_name") val versionName: String,
    @SerializedName("version_code") val versionCode: Int,
    @SerializedName("built_at") val builtAt: String,
    @SerializedName("flavor") val flavor: String,
    @SerializedName("workflow_id") val workflowId: String? = null,
)

@Keep
data class DeviceInfo(
    @SerializedName("android_version") val androidVersion: String,
    val manufacturer: String,
    val model: String,
)
