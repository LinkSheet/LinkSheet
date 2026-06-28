package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.viewmodel.util.LogViewCommon

class CrashHandlerViewerViewModel(
    context: Application,
    val logViewCommon: LogViewCommon,
    val logPersistService: LogPersistService
) : ViewModel() {
    val clipboardManager = context.getSystemService<ClipboardManager>()!!
}
