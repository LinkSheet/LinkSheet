package app.linksheet.feature.engine.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "expression_rule")
@TypeConverters(value = [ExpressionRuleType.Converter::class])
class ExpressionRule(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val bytes: ByteArray,
    val type: ExpressionRuleType
) {
}

enum class ExpressionRuleType {
    Pre, Post;

    companion object Converter {
        @TypeConverter
        fun toInt(id: ExpressionRuleType): Int {
            return id.ordinal
        }

        @TypeConverter
        fun toType(ordinal: Int): ExpressionRuleType {
            return entries[ordinal]
        }
    }
}
