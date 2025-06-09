package fe.linksheet.feature.sql

import fe.kotlin.extension.iterator.withElementInfo
import kotlin.collections.contains

class MarkdownTable {
    fun create(rows: List<SqlRow>): String {
        val (widths, uniqueColumns) = getWidth(rows)
        val table = createTable(uniqueColumns, rows, widths)

        return table
    }

    private fun createTableHeader(
        columns: Map<String, Column<*>>,
        widths: Map<String, Int>,
    ): Pair<StringBuilder, StringBuilder> {
        val header = StringBuilder()
        val header2 = StringBuilder()
        var i = 0
        for ((name, column) in columns) {
            val width = widths[name] ?: 0
            val paddedName = name.padEnd(width)
            val divider = "-".repeat(width)

            if (i == 0) {
                header.append("| ")
                header2.append("|-")
            } else {
                header.append(' ')
                header2.append('-')
            }

            header.append(paddedName).append(" |")
            header2.append(divider).append("-|")

            i++
        }

        return header to header2
    }

    private fun StringBuilder.appendRow(uniqueColumns: Map<String, Column<*>>, row: SqlRow, widths: Map<String, Int>) {
        var i = 0

        for ((name, _) in uniqueColumns) {
            val width = widths[name] ?: 0
            val column = row.columns[name]
            val value = column?.value?.toString() ?: ""
            val paddedValue = when (column) {
                is Column.StringValue -> value.padEnd(width)
                else -> value.padStart(width)
            }

            if (i == 0) {
                append("| ")
            } else {
                append(' ')
            }

            append(paddedValue).append(" |")
            i++
        }
    }

    private fun createTable(
        uniqueColumns: Map<String, Column<*>>,
        rows: List<SqlRow>,
        widths: Map<String, Int>,
    ): String {
        val (header, header2) = createTableHeader(uniqueColumns, widths)
        return buildString {
            for ((row, _, isFirst, isLast) in rows.withElementInfo()) {
                if (isFirst) {
                    appendLine(header)
                    appendLine(header2)
                }

                appendRow(uniqueColumns, row, widths)
                if (!isLast) appendLine()
            }
        }
    }

    private fun getWidth(rows: List<SqlRow>): Pair<Map<String, Int>, Map<String, Column<*>>> {
        val widths = mutableMapOf<String, Int>()
        val uniqueColumns = mutableMapOf<String, Column<*>>()
        for (row in rows) {
            for ((name, column) in row.columns) {
                val str = column.value.toString()
                var existing = widths[name]
                if (existing == null) {
                    widths.put(name, name.length)
                    existing = name.length
                }

                if (str.length > existing) {
                    widths.put(name, str.length)
                }

                if (name !in uniqueColumns) {
                    uniqueColumns.put(name, column)
                }
            }
        }

        return widths to uniqueColumns
    }
}
