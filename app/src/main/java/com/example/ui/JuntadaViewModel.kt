package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal

class JuntadaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = JuntadaRepository(database.juntadaDao())

    val allJuntadas: StateFlow<List<Juntada>> = repository.allJuntadas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedJuntadaId = MutableStateFlow<Int?>(null)
    val selectedJuntadaId: StateFlow<Int?> = _selectedJuntadaId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedJuntada: StateFlow<Juntada?> = _selectedJuntadaId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.getJuntadaById(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeGastos: StateFlow<List<Gasto>> = _selectedJuntadaId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getGastosForJuntada(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val splitResult: StateFlow<SplitResult?> = combine(selectedJuntada, activeGastos) { juntada, gastos ->
        if (juntada == null) null
        else SplitCalculator.calculateSplit(juntada.participants, gastos)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun selectJuntada(id: Int?) {
        _selectedJuntadaId.value = id
    }

    fun createJuntada(name: String, participants: List<String>) {
        viewModelScope.launch {
            val trimmedName = name.trim().ifEmpty { "Nueva Juntada" }
            val cleanParticipants = participants.map { it.trim() }.filter { it.isNotEmpty() }
            val newJuntada = Juntada(name = trimmedName, participants = cleanParticipants)
            val id = repository.insertJuntada(newJuntada)
            _selectedJuntadaId.value = id.toInt()
        }
    }

    fun updateJuntadaName(name: String) {
        val current = selectedJuntada.value ?: return
        viewModelScope.launch {
            val updated = current.copy(name = name.trim())
            repository.updateJuntada(updated)
        }
    }

    fun addParticipant(name: String) {
        val current = selectedJuntada.value ?: return
        val trimmed = name.trim()
        if (trimmed.isEmpty() || current.participants.any { it.equals(trimmed, ignoreCase = true) }) return
        viewModelScope.launch {
            val updatedParticipants = current.participants + trimmed
            val updated = current.copy(participants = updatedParticipants)
            repository.updateJuntada(updated)
        }
    }

    fun removeParticipant(name: String) {
        val current = selectedJuntada.value ?: return
        viewModelScope.launch {
            val updatedParticipants = current.participants.filterNot { it.equals(name, ignoreCase = true) }
            val updated = current.copy(participants = updatedParticipants)
            repository.updateJuntada(updated)
        }
    }

    fun addGasto(payerName: String, description: String, amount: BigDecimal) {
        val juntadaId = _selectedJuntadaId.value ?: return
        viewModelScope.launch {
            val newGasto = Gasto(
                juntadaId = juntadaId,
                payerName = payerName.trim(),
                description = description.trim().ifEmpty { "Gasto de $payerName" },
                amount = amount
            )
            repository.insertGasto(newGasto)
        }
    }

    fun deleteGasto(gasto: Gasto) {
        viewModelScope.launch {
            repository.deleteGasto(gasto)
        }
    }

    fun deleteJuntada(juntada: Juntada) {
        viewModelScope.launch {
            repository.deleteJuntada(juntada)
            if (_selectedJuntadaId.value == juntada.id) {
                _selectedJuntadaId.value = null
            }
        }
    }

    val allFriendGroups: StateFlow<List<FriendGroup>> = repository.allFriendGroups
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createFriendGroup(name: String, participants: List<String>) {
        viewModelScope.launch {
            val cleanParticipants = participants.map { it.trim() }.filter { it.isNotEmpty() }
            val newGroup = FriendGroup(
                name = name.trim().ifEmpty { "Nuevo Grupo" },
                participants = cleanParticipants
            )
            repository.insertFriendGroup(newGroup)
        }
    }

    fun updateFriendGroup(group: FriendGroup) {
        viewModelScope.launch {
            repository.updateFriendGroup(group)
        }
    }

    fun deleteFriendGroup(group: FriendGroup) {
        viewModelScope.launch {
            repository.deleteFriendGroup(group)
        }
    }
}
