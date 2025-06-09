package fe.linksheet.composable.page.settings.debug

import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.feature.sql.Column
import fe.linksheet.feature.sql.MarkdownTable
import fe.linksheet.feature.sql.SqlRow
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Test

internal class MarkdownTableTest : BaseUnitTest {
    @Test
    fun test() {
        val rows = listOf(
            SqlRow(
                "foo" to Column.StringValue("bar"),
                "test" to Column.IntValue(0)
            ),
            SqlRow(
                "foo" to Column.StringValue("world"),
                "test" to Column.IntValue(1337),
                "test2" to Column.FloatValue(1.5f)
            )
        )

        val table = MarkdownTable().create(rows)
        println(table)
        assertThat(table).isEqualTo("""| foo   | test | test2 |
|-------|------|-------|
| bar   |    0 |       |
| world | 1337 |   1.5 |""")
    }
}
