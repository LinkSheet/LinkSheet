package fe.linksheet.feature.systeminfo

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BuildInfo(
    @SerializedName("version_name")
    @SerialName("versionName")
    val versionName: String,
    @SerializedName("version_code")
    @SerialName("versionCode")
    val versionCode: Int,
    @SerializedName("built_at")
    @SerialName("builtAt")
    val builtAt: String,
    @SerializedName("flavor")
    @SerialName("flavor")
    val flavor: String,
    @SerializedName("workflow_id")
    @SerialName("workflowId")
    val workflowId: String? = null,
)

@Keep
@Serializable
data class DeviceInfo(
    @SerializedName("android_version")
    @SerialName("androidVersion")
    val androidVersion: String,
    @SerialName("manufacturer")
    val manufacturer: String,
    @SerialName("model")
    val model: String,
)
