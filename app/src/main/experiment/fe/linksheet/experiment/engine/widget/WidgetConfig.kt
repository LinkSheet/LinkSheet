package fe.linksheet.experiment.engine.widget

import fe.linksheet.experiment.engine.slot.AppRoleId

sealed interface WidgetConfig {
    val position: Int
}

sealed interface SingleSlotWidgetConfig : WidgetConfig {
}

sealed interface MultiSlotWidgetConfig : WidgetConfig {

}

interface Order {
    companion object : Order
}

class ListWidgetConfig(val order: Order, override val position: Int) : MultiSlotWidgetConfig {

}

class AppRoleSlotWidgetConfig(val role: AppRoleId, override val position: Int) : SingleSlotWidgetConfig {

}
