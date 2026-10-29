package app.linksheet.feature.engine.database.entity

import androidx.room3.ColumnTypeConverter
import androidx.room3.ColumnTypeConverters
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = ExpressionRule.TABLE_NAME)
@ColumnTypeConverters(value = [ExpressionRuleType.Converter::class])
class ExpressionRule(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val bytes: ByteArray,
    val type: ExpressionRuleType
) {
    companion object {
        const val TABLE_NAME = "expression_rule"
    }
}

enum class ExpressionRuleType {
    Pre, Post;

    companion object Converter {
        @ColumnTypeConverter
        fun toInt(id: ExpressionRuleType): Int {
            return id.ordinal
        }

        @ColumnTypeConverter
        fun toType(ordinal: Int): ExpressionRuleType {
            return entries[ordinal]
        }
    }
}
