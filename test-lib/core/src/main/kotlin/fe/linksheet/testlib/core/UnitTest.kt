package fe.linksheet.testlib.core

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatformTools
import org.koin.test.KoinTest
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.spi.FileSystemProvider

interface BaseUnitTest : KoinTest {

    val applicationContext: Context
        get() = ApplicationProvider.getApplicationContext()

    @Before
    @BeforeEach
    fun start() {
        closeJarFs()
    }

    @After
    @AfterEach
    fun stop() {
        val context = KoinPlatformTools.defaultContext()
        context.stopKoin()
        closeJarFs()
    }

    fun closeJarFs() {
        val fs = initFileSystem(URI("jar:file:/"))
//        println("Closing fs $fs")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            FileSystems.getFileSystem(URI("jar:file:/"))
//            runCatching { fs?.close() }
//        }
    }
}

class BaseUnitTestRule : TestWatcher() {
    override fun finished(description: Description?) {
        if (KoinPlatformTools.defaultContext().getOrNull() != null) {
            stopKoin()
        }
    }
}


typealias JunitTest = org.junit.Test
