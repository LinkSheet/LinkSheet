package dev.zwander.shared.shizuku

import android.content.Context
import android.os.UserHandle
import android.util.Log
import androidx.annotation.Keep
import dev.zwander.shared.IShizukuService
import fe.linksheet.util.AndroidVersion
import fe.processlauncher.launchProcess
import kotlin.system.exitProcess

class ShizukuService : IShizukuService.Stub {
    @Keep
    constructor() : super()

    @Keep
    constructor(@Suppress("UNUSED_PARAMETER") context: Context) : super()


    override fun setDomainState(packageName: String, domains: String, state: Int): Int {
        /**
         * From adb shell pm
         *
         * set-app-links [--package <PACKAGE>] <STATE> <DOMAINS>...
         *     Manually set the state of a domain for a package. The domain must be
         *     declared by the package as autoVerify for this to work. This command
         *     will not report a failure for domains that could not be applied.
         *       --package <PACKAGE>: the package to set, or "all" to set all packages
         *       <STATE>: the code to set the domains to, valid values are:
         *         STATE_NO_RESPONSE (0): reset as if no response was ever recorded.
         *         STATE_SUCCESS (1): treat domain as successfully verified by domain.
         *           verification agent. Note that the domain verification agent can
         *           override this.
         *         STATE_APPROVED (2): treat domain as always approved, preventing the
         *            domain verification agent from changing it.
         *         STATE_DENIED (3): treat domain as always denied, preveting the domain
         *           verification agent from changing it.
         *       <DOMAINS>: space separated list of domains to change, or "all" to
         *         change every domain.
         */
        if (AndroidVersion.AT_LEAST_API_31_S) {
            try {
                if (state == DomainVerificationState.STATE_APPROVED.state) {
                    val setAppLinkAllowedResult = launchProcess(
                        "pm", "set-app-links-allowed",
                        "--user",
                        getUserId(),
                        "--package", packageName, "true",
                    ) { line -> Log.d("ShizukuService", line) }
                }

                val setAppLinkStateResult = launchProcess(
                    "pm", "set-app-links",
                    "--package", packageName,
                    state.toString(),
                    domains
                ) { line -> Log.d("ShizukuService", line) }

                if (state == DomainVerificationState.STATE_NO_RESPONSE.state) {
                    val verifyAppLinksResult = launchProcess(
                        "pm", "verify-app-links", "--re-verify", packageName
                    ) { line -> Log.d("ShizukuService", line) }
                }

                // TODO: Properly return
                return 0
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        return -1
    }

    private fun getUserId(): String {
        return UserHandle::class.java.getMethod("myUserId").invoke(null)!!.toString()
    }

    override fun destroy() {
        exitProcess(0)
    }
}

enum class DomainVerificationState(val state: Int) {
    STATE_NO_RESPONSE(0),
    STATE_SUCCESS(1),
    STATE_APPROVED(2),
    STATE_DENIED(3)
}
