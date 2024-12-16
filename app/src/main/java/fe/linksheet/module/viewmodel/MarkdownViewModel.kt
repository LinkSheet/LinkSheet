package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import fe.httpkt.Request
import fe.httpkt.ext.isHttpSuccess
import fe.httpkt.ext.readToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkdownViewModel(
    val context: Application,
    val request: Request,
) : ViewModel() {

    suspend fun fetch(url: String): String? = withContext(Dispatchers.IO) {
        val response = request.get(url = url)
        if (!response.isHttpSuccess()) return@withContext null

        return@withContext response.readToString()
    }
}
