package fe.linksheet.activity.bottomsheet.compat

interface CompatSheetState {
    fun isAnimationRunning(): Boolean
    suspend fun expand()
    suspend fun partialExpand()
    suspend fun hide()
    fun isExpanded(): Boolean
}
