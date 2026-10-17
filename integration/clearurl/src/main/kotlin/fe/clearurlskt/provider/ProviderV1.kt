package fe.clearurlskt.provider

public data class ProviderV1(
    override val sortPosition: Int,
    override val key: String,
    override val url: Regex,
    override val completeProvider: Boolean,
    val rules: List<Regex>,
    override val rawRules: List<Regex>,
    override val referralMarketing: List<Regex>,
    override val exceptions: List<Regex>,
    override val redirections: List<Regex>,
) : Provider
