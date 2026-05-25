package com.example.data

import kotlinx.coroutines.flow.Flow

class JuntadaRepository(private val juntadaDao: JuntadaDao) {
    val allJuntadas: Flow<List<Juntada>> = juntadaDao.getAllJuntadas()

    fun getGastosForJuntada(juntadaId: Int): Flow<List<Gasto>> =
        juntadaDao.getGastosForJuntada(juntadaId)

    fun getJuntadaById(id: Int): Flow<Juntada?> =
        juntadaDao.getJuntadaById(id)

    suspend fun insertJuntada(juntada: Juntada): Long =
        juntadaDao.insertJuntada(juntada)

    suspend fun updateJuntada(juntada: Juntada) =
        juntadaDao.updateJuntada(juntada)

    suspend fun deleteJuntada(juntada: Juntada) =
        juntadaDao.deleteJuntada(juntada)

    suspend fun insertGasto(gasto: Gasto): Long =
        juntadaDao.insertGasto(gasto)

    suspend fun deleteGasto(gasto: Gasto) =
        juntadaDao.deleteGasto(gasto)

    val allFriendGroups: Flow<List<FriendGroup>> = juntadaDao.getAllFriendGroups()

    suspend fun insertFriendGroup(friendGroup: FriendGroup): Long =
        juntadaDao.insertFriendGroup(friendGroup)

    suspend fun updateFriendGroup(friendGroup: FriendGroup) =
        juntadaDao.updateFriendGroup(friendGroup)

    suspend fun deleteFriendGroup(friendGroup: FriendGroup) =
        juntadaDao.deleteFriendGroup(friendGroup)
}
