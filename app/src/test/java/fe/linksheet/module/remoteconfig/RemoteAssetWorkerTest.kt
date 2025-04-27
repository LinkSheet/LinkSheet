package fe.linksheet.module.remoteconfig

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.testing.TestListenableWorkerBuilder
import assertk.assertThat
import fe.linksheet.UnitTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class RemoteAssetWorkerTest : UnitTest {
    @Test
    fun test() {
        val worker = TestListenableWorkerBuilder<RemoteAssetFetcherWorker>(applicationContext).build()
        val result = worker.startWork().get()
        assertThat(result)
    }
}
