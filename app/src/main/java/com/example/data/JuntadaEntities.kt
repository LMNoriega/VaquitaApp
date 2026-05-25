package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.math.BigDecimal

@Entity(tableName = "juntadas")
data class Juntada(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dateCreated: Long = System.currentTimeMillis(),
    val participants: List<String> = emptyList()
)

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val juntadaId: Int,
    val payerName: String,
    val description: String,
    val amount: BigDecimal
)

@Entity(tableName = "friend_groups")
data class FriendGroup(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val participants: List<String> = emptyList()
)

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",").filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String {
        return value?.toPlainString() ?: "0"
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal {
        if (value.isNullOrEmpty()) return BigDecimal.ZERO
        return try {
            BigDecimal(value)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
}
