package com.pandu.allocap.ui.envelopes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandu.allocap.data.local.AllocationDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EnvelopeState(
    val needsUsed: Double = 0.0,
    val needsTarget: Float = 0.5f,
    val wantsUsed: Double = 0.0,
    val wantsTarget: Float = 0.3f,
    val savingsUsed: Double = 0.0,
    val savingsTarget: Float = 0.2f,
    val totalIncome: Double = 0.0
)

class EnvelopeViewModel(
    private val allocationDao: AllocationDao
) : ViewModel() {

    private val _state = MutableStateFlow(EnvelopeState())
    val state: StateFlow<EnvelopeState> = _state.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            val incomeFlow = allocationDao.getAllTransactions().map { transactions ->
                transactions.filter { it.isIncome }.sumOf { it.amount }
            }

            combine(
                allocationDao.getSumByCategory("Needs"),
                allocationDao.getSumByCategory("Wants"),
                allocationDao.getSumByCategory("Savings"),
                allocationDao.getAllocationSettings(),
                incomeFlow
            ) { needs, wants, savings, settings, income ->
                EnvelopeState(
                    needsUsed = needs ?: 0.0,
                    needsTarget = settings?.needsPercent ?: 0.5f,
                    wantsUsed = wants ?: 0.0,
                    wantsTarget = settings?.wantsPercent ?: 0.3f,
                    savingsUsed = savings ?: 0.0,
                    savingsTarget = settings?.savingsPercent ?: 0.2f,
                    totalIncome = if (income > 0) income else 1.0
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
