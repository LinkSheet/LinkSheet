package app.linksheet.api.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Builder
import androidx.room.migration.Migration

interface DatabaseCompanion<T : RoomDatabase> {
    private fun buildMigrations(): Array<Migration> {
        return arrayOf(
        )
    }

//    fun Builder<T>.configureAndBuild(): T {
//        return
////        addCallback(KnownInitCallback(ResolveType))
////            .addMigrations(*buildMigrations())
////            .build()
//    }
//
//    fun create(context: Context, name: String): T{
//        val db = Room
//            .databaseBuilder(context, T::class.java, name)
//            .configureAndBuild()
//
//        return db
//    }
}
