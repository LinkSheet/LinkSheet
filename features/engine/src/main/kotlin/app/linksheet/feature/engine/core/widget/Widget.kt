package app.linksheet.feature.engine.core.widget

import app.linksheet.feature.app.ActivityAppInfo

sealed interface Widget {
}

class ListWidget(val order: Order, val appInfos: List<ActivityAppInfo>) : Widget {

}

class AppRoleSlotWidget(val appInfo: ActivityAppInfo) : Widget {

}
