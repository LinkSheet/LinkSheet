package app.linksheet.sdk

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.reflect.KClass

object MessageHandlers {
//    val PreRule = PreRuleMessageHandler
//    val PostRule = PostRuleMessageHandler
//    private val handlers: List<ContentProviderMessageHandler<out Message>> = listOf(PreRule, PostRule)
//
//    fun getHandler(type: String): ContentProviderMessageHandler<out Message>? {
//        return handlers.firstOrNull { it.type == type }
//    }
    val PreRule = "pre_rule"
    val PostRule = "post_rule"
}

data class PluginMethod<I : Message>(val name: String, val input: KClass<I>) {
    companion object{
        val PreRule = PluginMethod("pre_rule", PreRuleMessage::class)
        val PostRule = PluginMethod("post_rule", PostRuleMessage::class)
    }
}

@Parcelize
sealed interface Message : Parcelable

sealed interface Exchange<I : Message, O : Message>

class PreRuleExchange : Exchange<PreRuleExchange.Input, PreRuleExchange.Output> {
    class Input(val url: String) : Message
    class Output : Message
}

sealed interface ContentProviderMessageHandler<M : Message> {
    val type: String
    fun toBundle(data: M): Bundle
    fun fromBundle(bundle: Bundle): M?
}

data class PreRuleMessage(val url: String) : Message
data class PostRuleMessage(val originalUrl: String, val resultUrl: String) : Message

object PreRuleMessageHandler : ContentProviderMessageHandler<PreRuleMessage> {
    override val type = "pre_rule"
    override fun toBundle(data: PreRuleMessage): Bundle {
        return Bundle().apply {
            putString("url", data.url)
        }
    }

    override fun fromBundle(bundle: Bundle): PreRuleMessage? {
        val url = bundle.getString("url") ?: return null
        return PreRuleMessage(url)
    }
}

object PostRuleMessageHandler : ContentProviderMessageHandler<PostRuleMessage> {
    override val type = "post_rule"
    override fun toBundle(data: PostRuleMessage): Bundle {
        return Bundle().apply {
            putString("original_url", data.originalUrl)
            putString("result_url", data.resultUrl)
        }
    }

    override fun fromBundle(bundle: Bundle): PostRuleMessage? {
        val originalUrl = bundle.getString("original_url") ?: return null
        val resultUrl = bundle.getString("result_url") ?: return null
        return PostRuleMessage(originalUrl, resultUrl)
    }
}
