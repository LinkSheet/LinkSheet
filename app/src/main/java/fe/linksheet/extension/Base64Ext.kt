package fe.linksheet.extension

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun Base64.Default.encodeFromString(input: String) = Base64.encode(input.toByteArray())

@OptIn(ExperimentalEncodingApi::class)
fun Base64.Default.decodeToString(input: String) = String(Base64.decode(input.toByteArray()))