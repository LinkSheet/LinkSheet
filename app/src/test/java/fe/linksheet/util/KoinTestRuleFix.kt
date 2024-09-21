package fe.linksheet.util

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

@OptIn(KoinInternalApi::class)
class KoinTestRuleFix private constructor(private val appDeclaration: KoinAppDeclaration) : TestWatcher() {

    private var _koin: Koin? = null
    val koin: Koin
        get() = _koin ?: error("No Koin application found")

    override fun starting(description: Description?) {
        stopKoin()
        _koin = startKoin(appDeclaration = appDeclaration).koin
        koin.logger.info("Koin Rule - starting")
    }

    override fun finished(description: Description?) {
        koin.logger.info("Koin Rule - finished")
        stopKoin()
        _koin = null
    }

    companion object {
        fun create(appDeclaration: KoinAppDeclaration = {}): KoinTestRuleFix {
            return KoinTestRuleFix(appDeclaration)
        }
    }
}
