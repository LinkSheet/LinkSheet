package fe.linksheet.module.viewmodel

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.index
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import fe.linksheet.composable.page.settings.debug.log.PrefixMessageCardContent
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest


internal class LogEntryMergerTest : BaseUnitTest {
    private fun Assert<PrefixMessageCardContent>.assertContent(prefix: String, type: String, messages: Int) {
        all {
            prop(PrefixMessageCardContent::prefix).isEqualTo(prefix)
            prop(PrefixMessageCardContent::type).isEqualTo(type)
            prop(PrefixMessageCardContent::messages).hasSize(messages)
        }
    }

    @org.junit.Test
    fun test() {
        val entries = listOf(
            LogEntry.DefaultLogEntry(
                "Service1",
                1745587375444,
                "D",
                "Log1",
                "Log1"
            ),
            LogEntry.DefaultLogEntry(
                "Service2",
                1745587375984,
                "I",
                "Log2",
                "Log2",
            ),
            LogEntry.DefaultLogEntry(
                "Service3",
                1745587376005,
                "E",
                "Log3",
                "Log3"
            )
        )
        val merger = LogEntryMerger()
        val contents = merger.mergeEntries(entries)

        assertThat(contents).all {
            hasSize(3)
            index(0).assertContent("D", "Service1", 1)
            index(1).assertContent("I", "Service2", 1)
            index(2).assertContent("E", "Service3", 1)
        }
    }

    @org.junit.Test
    fun test2() {
        val entries = listOf(
            LogEntry.DefaultLogEntry(
                "Service1",
                1745587375444,
                "D",
                "Log1",
                "Log1"
            ),
            LogEntry.DefaultLogEntry(
                "Service1",
                1745587375445,
                "D",
                "Log1-2",
                "Log1-2"
            ),
            LogEntry.DefaultLogEntry(
                "Service2",
                1745587375984,
                "I",
                "Log2",
                "Log2",
            ),
            LogEntry.DefaultLogEntry(
                "Service3",
                1745587376005,
                "E",
                "Log3",
                "Log3"
            )
        )
        val merger = LogEntryMerger()
        val contents = merger.mergeEntries(entries)

        assertThat(contents).all {
            index(0).assertContent("D", "Service1", 2)
            index(1).assertContent("I", "Service2", 1)
            index(2).assertContent("E", "Service3", 1)
        }
    }
}
