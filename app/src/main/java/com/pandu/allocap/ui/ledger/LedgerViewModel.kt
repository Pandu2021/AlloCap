package com.pandu.allocap.ui.ledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandu.allocap.data.local.AllocationDao
import com.pandu.allocap.data.model.Transaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

enum class TimeFilter { DAILY, WEEKLY, MONTHLY }

data class LedgerState(
    val selectedFilter: TimeFilter = TimeFilter.DAILY,
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val isLoading: Boolean = true
)

class LedgerViewModel(
    private val allocationDao: AllocationDao
) : ViewModel() {

    private val _filter = MutableStateFlow(TimeFilter.DAILY)
    val state: StateFlow<LedgerState> = _filter
        .flatMapLatest { filter ->
            val startTime = getStartTimeForFilter(filter)
            allocationDao.getTransactionsAfter(startTime).map { list ->
                val income = list.filter { it.isIncome }.sumOf { it.amount }
                val expense = list.filter { !it.isIncome }.sumOf { it.amount }
                LedgerState(
                    selectedFilter = filter,
                    transactions = list,
                    totalIncome = income,
                    totalExpense = expense,
                    netBalance = income - expense,
                    isLoading = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LedgerState())

    fun setFilter(filter: TimeFilter) {
        _filter.value = filter
    }

    private fun getStartTimeForFilter(filter: TimeFilter): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (filter) {
            TimeFilter.DAILY -> { /* Today's start is already set */ }
            TimeFilter.WEEKLY -> calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            TimeFilter.MONTHLY -> calendar.set(Calendar.DAY_OF_MONTH, 1)
        }
        return calendar.timeInMillis
    }
}
