package fe.linksheet.experiment.engine.widget

import fe.linksheet.experiment.engine.context.AppRoleId

sealed interface WidgetConfig<W : Widget> {
    val position: Int
}

sealed interface SingleSlotWidgetConfig<W : Widget> : WidgetConfig<W> {
}

sealed interface MultiSlotWidgetConfig<W : Widget> : WidgetConfig<W> {

}

interface Order {
    companion object : Order
}

class ListWidgetConfig(
    val order: Order,
    override val position: Int,
) : MultiSlotWidgetConfig<ListWidget> {

}

class AppRoleSlotWidgetConfig(
    val role: AppRoleId,
    override val position: Int,
) : SingleSlotWidgetConfig<AppRoleSlotWidget> {

}
