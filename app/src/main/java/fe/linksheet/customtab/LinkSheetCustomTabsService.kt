package fe.linksheet.customtab

import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsSessionToken
import mozilla.components.support.base.log.logger.Logger
import org.koin.core.component.KoinComponent

class LinkSheetCustomTabsService : CustomTabsService(), KoinComponent {
    private val logger = Logger("LinkSheetCustomTabsService")

    override fun warmup(flags: Long): Boolean {
        logger.debug("⇢ warmup($flags)")
        return true
    }

    override fun newSession(sessionToken: CustomTabsSessionToken): Boolean {
        logger.debug("⇢ newSession($sessionToken)")
        return true
    }

    override fun mayLaunchUrl(
        sessionToken: CustomTabsSessionToken,
        url: Uri?,
        extras: Bundle?,
        otherLikelyBundles: MutableList<Bundle>?,
    ): Boolean {
        logger.debug("⇢ mayLaunchUrl($sessionToken, $url, $extras, $otherLikelyBundles)")
        return true
    }

    override fun extraCommand(commandName: String, args: Bundle?): Bundle? {
        logger.debug("⇢ extraCommand($commandName, $args)")
        return null
    }
    override fun requestPostMessageChannel(sessionToken: CustomTabsSessionToken, postMessageOrigin: Uri): Boolean {
        logger.debug("⇢ requestPostMessageChannel($sessionToken, $postMessageOrigin)")
        return true
    }

    override fun postMessage(sessionToken: CustomTabsSessionToken, message: String, extras: Bundle?): Int {
        logger.debug("⇢ postMessage($sessionToken, $message, $extras)")
        return RESULT_SUCCESS
    }

    override fun validateRelationship(
        sessionToken: CustomTabsSessionToken,
        relation: Int,
        origin: Uri,
        extras: Bundle?,
    ): Boolean {
        logger.debug("⇢ validateRelationship($sessionToken, $relation, $origin, $extras)")
        return true
    }

    override fun updateVisuals(sessionToken: CustomTabsSessionToken, bundle: Bundle?): Boolean {
        logger.debug("⇢ updateVisuals($sessionToken, $bundle)")
        return true
    }

    override fun receiveFile(sessionToken: CustomTabsSessionToken, uri: Uri, purpose: Int, extras: Bundle?): Boolean {
        logger.debug("⇢ updateVisuals($sessionToken, $uri, $purpose, $extras)")
        return true
    }
}
