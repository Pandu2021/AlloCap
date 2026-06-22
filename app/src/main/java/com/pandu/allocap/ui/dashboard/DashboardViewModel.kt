package com.pandu.allocap.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandu.allocap.data.UserPreferencesRepository
import com.pandu.allocap.data.local.AllocationDao
import com.pandu.allocap.data.model.Transaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class DashboardState(
    val userName: String = "",
    val totalCapital: Double = 0.0,
    val totalIncome: Double = 0.0,
    val needsAmount: Double = 0.0,
    val wantsAmount: Double = 0.0,
    val savingsAmount: Double = 0.0,
    val needsActualPercent: Float = 0f,
    val wantsActualPercent: Float = 0f,
    val savingsActualPercent: Float = 0f,
    val needsTarget: Float = 0.5f,
    val wantsTarget: Float = 0.3f,
    val savingsTarget: Float = 0.2f,
    val recentTransactions: List<Transaction> = emptyList(),
    val greeting: String = "",
    val aiAdvice: String? = null
)

class DashboardViewModel(
    private val allocationDao: AllocationDao,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            // Flow for total income
            val incomeFlow = allocationDao.getAllTransactions().map { transactions ->
                transactions.filter { it.isIncome }.sumOf { it.amount }
            }

            // Flow for amounts by category
            val financialDataFlow = combine(
                allocationDao.getTotalCapital(),
                allocationDao.getSumByCategory("Needs"),
                allocationDao.getSumByCategory("Wants"),
                allocationDao.getSumByCategory("Savings"),
                incomeFlow
            ) { total, needs, wants, savings, income ->
                val n = needs ?: 0.0
                val w = wants ?: 0.0
                val s = savings ?: 0.0
                // If no income recorded, use sum of spending as temporary base or at least 1.0 to avoid div by zero
                val base = if (income > 0) income else (n + w + s).coerceAtLeast(1.0)
                
                DataSnapshot(
                    total = total ?: 0.0,
                    income = income,
                    needs = n,
                    wants = w,
                    savings = s,
                    baseForPercent = base
                )
            }

            combine(
                preferencesRepository.userNameFlow,
                financialDataFlow,
                allocationDao.getAllocationSettings(),
                allocationDao.getAllTransactions().map { it.take(5) }
            ) { name, financial, settings, transactions ->
                val nTarget = settings?.needsPercent ?: 0.5f
                val wTarget = settings?.wantsPercent ?: 0.3f
                val sTarget = settings?.savingsPercent ?: 0.2f
                
                val nActual = (financial.needs / financial.baseForPercent).toFloat()
                val wActual = (financial.wants / financial.baseForPercent).toFloat()
                val sActual = (financial.savings / financial.baseForPercent).toFloat()

                DashboardState(
                    userName = name ?: "Guest",
                    totalCapital = financial.total,
                    totalIncome = financial.income,
                    needsAmount = financial.needs,
                    wantsAmount = financial.wants,
                    savingsAmount = financial.savings,
                    needsActualPercent = nActual,
                    wantsActualPercent = wActual,
                    savingsActualPercent = sActual,
                    needsTarget = nTarget,
                    wantsTarget = wTarget,
                    savingsTarget = sTarget,
                    recentTransactions = transactions,
                    greeting = getGreeting(),
                    aiAdvice = generateAIAdvice(nActual, wActual, sActual, nTarget, wTarget, sTarget)
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    private data class DataSnapshot(
        val total: Double,
        val income: Double,
        val needs: Double,
        val wants: Double,
        val savings: Double,
        val baseForPercent: Double
    )

    private fun generateAIAdvice(
        actualN: Float, actualW: Float, actualS: Float,
        targetN: Float, targetW: Float, targetS: Float
    ): String? {
        val tips = mutableListOf<String>()
        
        if (actualN > targetN + 0.05f) {
            tips.add("Your essential expenses (Needs) are at ${(actualN * 100).toInt()}%, which is slightly above your ${(targetN * 100).toInt()}% strategy limit.")
        }
        if (actualW > targetW + 0.03f) {
            tips.add("Lifestyle spending (Wants) has reached ${(actualW * 100).toInt()}%. Restricting non-essential outflow could accelerate your capital growth.")
        }
        if (actualS < targetS - 0.05f) {
            tips.add("Your current savings rate is ${(actualS * 100).toInt()}%. Prioritizing your ${(targetS * 100).toInt()}% goal is recommended for long-term security.")
        }

        return if (tips.isNotEmpty()) {
            "AI Financial Insight: " + tips.joinToString(" ") + " Balanced allocation is the foundation of wealth."
        } else if (actualS > 0) {
            "AI Financial Insight: Your current capital allocation perfectly aligns with your financial strategy. Keep up the disciplined management."
        } else {
            null
        }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    fun saveTransaction(transaction: Transaction) {
        viewModelScope.launch {
            allocationDao.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            allocationDao.deleteTransaction(transaction)
        }
    }
}
