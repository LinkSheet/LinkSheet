package fe.clearurlskt.provider

public sealed interface Provider {
    public val sortPosition: Int
    public val key: String
    public val url: Regex
    public val completeProvider: Boolean
    public val rawRules: List<Regex>
    public val referralMarketing: List<Regex>
    public val exceptions: List<Regex>
    public val redirections: List<Regex>
}
