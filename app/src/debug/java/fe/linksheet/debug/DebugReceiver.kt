package fe.linksheet.debug

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.linksheet.LinkSheetApp
import fe.linksheet.extension.android.queryAllResolveInfos
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class DebugReceiver : BroadcastReceiver(), KoinComponent {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val COPY_URL_BROADCAST = "fe.linksheet.debug.COPY_URL"
        private const val RESOLVE_URL_BROADCAST = "fe.linksheet.debug.RESOLVE_URL"
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DebugReceiver", "$context $intent")
        val app = get<LinkSheetApp>()

        if (intent.action == COPY_URL_BROADCAST) {
            val clipboardManager = context.getSystemService<ClipboardManager>()!!
            val url = intent.extras?.getString("url")
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Debug intent", url))

            return
        }

        if (intent.action == RESOLVE_URL_BROADCAST) {
            val viewModel = BottomSheetViewModel(get(), get(), get(), get(), get(), SavedStateHandle())
            val url = intent.extras?.getString("url")
            val gson = get<Gson>()


            val uri = Uri.parse(url)
            val resolveIntent = Intent(Intent.ACTION_VIEW, uri)
//            val unfurler = Unfurler().unfurl()

//            BottomSheetResult.BottomSheetSuccessResult(resolveIntent, uri, )
            val clipboardManager = context.getSystemService<ClipboardManager>()!!

            coroutineScope.launch(Dispatchers.IO) {
                val result = viewModel.resolveAsync(resolveIntent, null).await()
                if (result is BottomSheetResult.BottomSheetSuccessResult) {
                    val item = context.packageManager.queryAllResolveInfos().associate {
                        it.activityInfo.packageName to Pair(
                            it,
                            it.loadLabel(context.packageManager).toString()
                        )
                    }

//                    val ctx = GlobalGsonContext
//
//                    GlobalGsonContext.configure {
//                        UriTypeAdapter.register(this)
//                        HttpUrlTypeAdapter.register(this)
//                    }

                    val obj = jsonObject {
                        "intent" += result.intent
                        "uri" += result.uri.toString()
                        "unfurl" += result.unfurlResult
                        "resolved" += result.resolved.map { it.resolvedInfo.activityInfo.packageName }
                        "filteredItem" += result.filteredItem?.packageName
                    }

                    val json = obj.toString()
                    val base64 = Base64.Default.encode(json.encodeToByteArray())
                    base64.chunked(4000).forEach {
                        Log.d("DebugReceiver", it)
                    }

                    json.chunked(4000).forEach {
                        Log.d("DebugReceiver", it)
                    }

                    clipboardManager.setPrimaryClip(ClipData.newPlainText("Resolve result", json))

                    setResult(0, json, Bundle.EMPTY)
                }
            }

            return
        }

//            context.startActivity(Intent(context, BottomSheetActivity::class.java).apply {
//                this.action = Intent.ACTION_VIEW
//                this.data = Uri.parse(url)
//                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            })

    }

}
