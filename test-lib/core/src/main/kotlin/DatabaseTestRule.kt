package app.linksheet.testlib.rule

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.reflect.KClass

inline fun <reified T : RoomDatabase> DatabaseTestRule(
    applicationContext: Context,
    noinline config: (RoomDatabase.Builder<T>) -> T = { it.build() }
): DatabaseTestRule<T> {
    return DatabaseTestRule(applicationContext, T::class, config)
}

class DatabaseTestRule<T : RoomDatabase>(
    private val applicationContext: Context,
    private val clazz: KClass<T>,
    private val config: (RoomDatabase.Builder<T>) -> T
) : TestWatcher() {

    val database: T = createInMemoryTestDatabase(applicationContext, clazz)

    fun createInMemoryTestDatabase(context: Context, clazz: KClass<T>): T {
        val database = Room.inMemoryDatabaseBuilder(context, clazz.java)
        return config(database)
    }

    override fun starting(description: Description?) {
    }

    override fun finished(description: Description?) {
        database.close()
    }
}
