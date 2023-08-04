package dev.zwander.shared.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerifyResult(
    val output: List<String>,
    val result: Int,
) : Parcelable
