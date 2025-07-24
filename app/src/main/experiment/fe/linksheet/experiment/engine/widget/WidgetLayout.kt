package fe.linksheet.experiment.engine.widget

import fe.linksheet.experiment.engine.context.AppRoleId
import fe.linksheet.feature.app.ActivityAppInfo


typealias ActivityAppInfoWithDefaults = Map<ActivityAppInfo, Set<AppRoleId>>

class WidgetLayout(
    private val config: List<WidgetConfig<*>>,
) {
    fun create(apps: ActivityAppInfoWithDefaults): List<Widget> {
        val mutApps = apps.toMutableMap()
        val widgets = mutableListOf<Widget>()

        val (singleSlot, multiSlot) = config.partition { it is SingleSlotWidgetConfig }
        for (widget in singleSlot.sortedBy { it.position }) {
            if (widget !is SingleSlotWidgetConfig) continue
            val (widget, info) = handle(widget, apps) ?: continue
            mutApps.remove(info)
            widgets.add(widget)
        }

        for (widget in multiSlot.sortedBy { it.position }) {
            if (widget !is MultiSlotWidgetConfig) continue
            val (widget, infos) = handle(widget, apps)
            for (info in infos) {
                mutApps.remove(info)
            }
            widgets.add(widget)
        }

        return widgets
    }

    private fun <W : Widget> handle(
        widget: SingleSlotWidgetConfig<W>,
        apps: ActivityAppInfoWithDefaults,
    ): Pair<AppRoleSlotWidget, ActivityAppInfo>? {
        when (widget) {
            is AppRoleSlotWidgetConfig -> {
                val candidates = apps.filter { widget.role in it.value }
                val pick = candidates.keys.singleOrNull()
                if (pick != null) {
                    return AppRoleSlotWidget(pick) to pick
                }
            }
        }

        return null
    }

    private fun <W : Widget> handle(
        widget: MultiSlotWidgetConfig<W>,
        apps: ActivityAppInfoWithDefaults,
    ): Pair<ListWidget, List<ActivityAppInfo>> {
        when (widget) {
            is ListWidgetConfig -> {
                val infos = apps.keys.toList()
                return ListWidget(widget.order, infos) to infos
            }
        }
    }
}
