package com.pandu.allocap.ui.sandbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandu.allocap.data.local.AllocationDao
import com.pandu.allocap.data.model.AllocationSettings
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SandboxState(
    val needs: Float = 0.5f,
    val wants: Float = 0.3f,
    val savings: Float = 0.2f,
    val isDirty: Boolean = false,
    val showSuccess: Boolean = false
)

class SandboxViewModel(
    private val allocationDao: AllocationDao
) : ViewModel() {

    private val _state = MutableStateFlow(SandboxState())
    val state: StateFlow<SandboxState> = _state.asStateFlow()

    init {
        loadInitialSettings()
    }

    private fun loadInitialSettings() {
        viewModelScope.launch {
            allocationDao.getAllocationSettings().firstOrNull()?.let { settings ->
                _state.update { 
                    it.copy(
                        needs = settings.needsPercent,
                        wants = settings.wantsPercent,
                        savings = settings.savingsPercent,
                        isDirty = false
                    )
                }
            }
        }
    }

    fun updateNeeds(value: Float) {
        val delta = value - _state.value.needs
        val remaining = 1.0f - value
        val currentOthers = _state.value.wants + _state.value.savings
        
        val (newWants, newSavings) = if (currentOthers > 0) {
            val wRatio = _state.value.wants / currentOthers
            val sRatio = _state.value.savings / currentOthers
            Pair(remaining * wRatio, remaining * sRatio)
        } else {
            Pair(remaining / 2, remaining / 2)
        }

        _state.update { it.copy(needs = value, wants = newWants, savings = newSavings, isDirty = true) }
    }

    fun updateWants(value: Float) {
        val remaining = 1.0f - value
        val currentOthers = _state.value.needs + _state.value.savings
        
        val (newNeeds, newSavings) = if (currentOthers > 0) {
            val nRatio = _state.value.needs / currentOthers
            val sRatio = _state.value.savings / currentOthers
            Pair(remaining * nRatio, remaining * sRatio)
        } else {
            Pair(remaining / 2, remaining / 2)
        }

        _state.update { it.copy(wants = value, needs = newNeeds, savings = newSavings, isDirty = true) }
    }

    fun updateSavings(value: Float) {
        val remaining = 1.0f - value
        val currentOthers = _state.value.needs + _state.value.wants
        
        val (newNeeds, newWants) = if (currentOthers > 0) {
            val nRatio = _state.value.needs / currentOthers
            val wRatio = _state.value.wants / currentOthers
            Pair(remaining * nRatio, remaining * wRatio)
        } else {
            Pair(remaining / 2, remaining / 2)
        }

        _state.update { it.copy(savings = value, needs = newNeeds, wants = newWants, isDirty = true) }
    }

    fun applySettings() {
        viewModelScope.launch {
            val s = _state.value
            allocationDao.updateAllocationSettings(
                AllocationSettings(
                    needsPercent = s.needs,
                    wantsPercent = s.wants,
                    savingsPercent = s.savings
                )
            )
            _state.update { it.copy(isDirty = false, showSuccess = true) }
        }
    }
    
    fun dismissSuccess() {
        _state.update { it.copy(showSuccess = false) }
    }
}
