package fe.linksheet.activity.bottomsheet.impl

interface CompatSheetState {
    fun isAnimationRunning(): Boolean
    suspend fun expand()
    suspend fun partialExpand()
    suspend fun hide()
    fun isExpanded(): Boolean
}
