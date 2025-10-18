package fe.clearurlskt.provider

public data class Provider(
    val sortPosition: Int,
    val key: String,
    val url: Regex,
    val completeProvider: Boolean,
    val rules: List<Regex>,
    val rawRules: List<Regex>,
    val referralMarketing: List<Regex>,
    val exceptions: List<Regex>,
    val redirections: List<Regex>,
)
