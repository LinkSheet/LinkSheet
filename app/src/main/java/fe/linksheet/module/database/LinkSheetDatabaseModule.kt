package fe.linksheet.module.database

import androidx.room.Room
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.migrations.Migration1to2
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), LinkSheetDatabase::class.java, "linksheet")
            .addMigrations(Migration1to2).build()
    }
}