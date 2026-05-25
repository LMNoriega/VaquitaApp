package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JuntadaDao {
    @Query("SELECT * FROM juntadas ORDER BY dateCreated DESC")
    fun getAllJuntadas(): Flow<List<Juntada>>

    @Query("SELECT * FROM juntadas WHERE id = :id")
    fun getJuntadaById(id: Int): Flow<Juntada?>

    @Query("SELECT * FROM gastos WHERE juntadaId = :juntadaId")
    fun getGastosForJuntada(juntadaId: Int): Flow<List<Gasto>>

    @Query("SELECT * FROM gastos WHERE juntadaId = :juntadaId")
    suspend fun getGastosForJuntadaSync(juntadaId: Int): List<Gasto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJuntada(juntada: Juntada): Long

    @Update
    suspend fun updateJuntada(juntada: Juntada)

    @Delete
    suspend fun deleteJuntada(juntada: Juntada)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasto(gasto: Gasto): Long

    @Delete
    suspend fun deleteGasto(gasto: Gasto)

    @Query("SELECT * FROM friend_groups ORDER BY name ASC")
    fun getAllFriendGroups(): Flow<List<FriendGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendGroup(friendGroup: FriendGroup): Long

    @Update
    suspend fun updateFriendGroup(friendGroup: FriendGroup)

    @Delete
    suspend fun deleteFriendGroup(friendGroup: FriendGroup)
}
