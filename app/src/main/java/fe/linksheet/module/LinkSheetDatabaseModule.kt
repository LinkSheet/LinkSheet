package fe.linksheet.module

import com.tasomaniac.openwith.data.LinkSheetDatabase
import org.koin.core.scope.get
import org.koin.dsl.module

val linkSheetDatabaseModule = module {
    single {
        LinkSheetDatabase.getDatabase(get())
    }
}