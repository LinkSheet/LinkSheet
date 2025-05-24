package fe.linksheet.experiment.engine.widget

import fe.linksheet.module.app.ActivityAppInfo

sealed interface Widget {
}

class ListWidget(val order: Order, val appInfos: List<ActivityAppInfo>) : Widget{

}

class AppRoleSlotWidget(val appInfo: ActivityAppInfo) : Widget{

}
