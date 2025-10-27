package fe.linksheet.feature.engine.engine

import app.linksheet.feature.engine.engine.context.AppRole
import app.linksheet.feature.engine.engine.context.AppRoleId
import app.linksheet.feature.engine.engine.context.EngineExtra
import app.linksheet.feature.engine.engine.context.EngineFlag
import app.linksheet.feature.engine.engine.context.EngineRunContext
import app.linksheet.feature.engine.engine.context.SealedRunContext
import app.linksheet.feature.engine.engine.fetcher.ContextResult
import app.linksheet.feature.engine.engine.fetcher.ContextResultId
import fe.linksheet.util.AndroidAppPackage
import fe.std.extension.emptyEnumSet
import java.util.EnumSet

//TODO: Remove (duplicate)
suspend fun <T, R> withTestRunContext(it: T, block: suspend T.(EngineRunContext) -> R): R {
    return it.block(TestEngineRunContext)
}

object TestEngineRunContext : EngineRunContext {
    override val extras: Set<EngineExtra> = emptySet()
    override val flags: EnumSet<EngineFlag> = emptyEnumSet()
    override var allowCustomTab: Boolean? = null
    override val roles: MutableSet<AppRole> = mutableSetOf()

    override fun put(id: AppRoleId, appPackage: AndroidAppPackage): Boolean = true
    override fun <Result : ContextResult> put(id: ContextResultId<Result>, result: Result?) {}
    override fun <Result : ContextResult> confirm(fetcher: ContextResultId<Result>): Boolean = true
    override fun seal(): SealedRunContext = SealedRunContext(flags, roles, allowCustomTab, emptyMap())
}
