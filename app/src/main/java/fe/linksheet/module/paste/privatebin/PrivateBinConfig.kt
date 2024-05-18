package fe.linksheet.module.paste.privatebin

data class PrivateBinConfig(
    val baseUrl: String,
    val kdfIterations: Int,
    val aesSize: Int,
    val tagLength: Int,
    val expire: String,
    val format: String,
    val openDiscussion: Int,
    val burnAfterRead: Int,
) {
    companion object {
        val Default = PrivateBinConfig(
            "https://privatebin.net",
            100_000,
            256,
            128,
            "1month",
            "plaintext",
            0,
            0
        )
    }
}
