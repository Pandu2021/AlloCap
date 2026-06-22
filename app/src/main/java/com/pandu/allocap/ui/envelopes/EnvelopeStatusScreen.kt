package com.pandu.allocap.ui.envelopes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.ui.theme.AlloCapTheme
import com.pandu.allocap.ui.theme.ColorNeeds
import com.pandu.allocap.ui.theme.ColorWants
import com.pandu.allocap.ui.theme.ColorSavings
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.PaleSageMint
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvelopeStatusScreen(
    viewModel: EnvelopeViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Envelope Status", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaleSageMint)
            )
        },
        containerColor = PaleSageMint
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Track your budget limits and allocation health.",
                style = MaterialTheme.typography.bodyMedium,
                color = DeepSpruce.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Needs Envelope
            EnvelopeStatusCard(
                label = "Essential Needs",
                used = state.needsUsed,
                targetPercent = state.needsTarget,
                totalIncome = state.totalIncome,
                color = ColorNeeds
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Wants Envelope
            EnvelopeStatusCard(
                label = "Lifestyle Wants",
                used = state.wantsUsed,
                targetPercent = state.wantsTarget,
                totalIncome = state.totalIncome,
                color = ColorWants
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Savings Envelope
            EnvelopeStatusCard(
                label = "Future Savings",
                used = state.savingsUsed,
                targetPercent = state.savingsTarget,
                totalIncome = state.totalIncome,
                color = ColorSavings
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            HealthSummarySection(state)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun EnvelopeStatusCard(
    label: String,
    used: Double,
    targetPercent: Float,
    totalIncome: Double,
    color: Color
) {
    val budgetLimit = totalIncome * targetPercent
    val progress = (used / budgetLimit).toFloat().coerceIn(0f, 1f)
    val remaining = (budgetLimit - used).coerceAtLeast(0.0)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, fontWeight = FontWeight.Bold, color = DeepSpruce, fontSize = 16.sp)
                Text(
                    text = "${(targetPercent * 100).toInt()}% Strategy",
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Used", style = MaterialTheme.typography.labelSmall, color = DeepSpruce.copy(alpha = 0.5f))
                    Text(
                        text = "$${String.format(Locale.US, "%.0f", used)}",
                        fontWeight = FontWeight.Bold,
                        color = DeepSpruce
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Remaining", style = MaterialTheme.typography.labelSmall, color = DeepSpruce.copy(alpha = 0.5f))
                    Text(
                        text = "$${String.format(Locale.US, "%.0f", remaining)}",
                        fontWeight = FontWeight.Bold,
                        color = if (remaining < budgetLimit * 0.1) Color(0xFFE57373) else DeepSpruce
                    )
                }
            }
        }
    }
}

@Composable
fun HealthSummarySection(state: EnvelopeState) {
    val totalUsed = state.needsUsed + state.wantsUsed
    val totalBudget = state.totalIncome * (state.needsTarget + state.wantsTarget)
    val overallHealth = if (totalUsed < totalBudget * 0.8) "Excellent" else if (totalUsed < totalBudget) "Fair" else "Critical"
    val healthColor = if (overallHealth == "Excellent") Color(0xFF4CAF50) else if (overallHealth == "Fair") ColorWants else Color(0xFFE57373)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(healthColor.copy(alpha = 0.1f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Overall Health", style = MaterialTheme.typography.labelLarge, color = healthColor)
        Text(
            text = overallHealth,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = healthColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You have utilized ${( (totalUsed / state.totalIncome) * 100).toInt()}% of your total capital inflow this month.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = DeepSpruce.copy(alpha = 0.7f)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun EnvelopeStatusPreview() {
    AlloCapTheme {
        EnvelopeStatusScreen(
            viewModel = EnvelopeViewModel(object : com.pandu.allocap.data.local.AllocationDao {
                override fun getAllocationSettings() = kotlinx.coroutines.flow.flowOf(null)
                override suspend fun updateAllocationSettings(settings: com.pandu.allocap.data.model.AllocationSettings) {}
                override fun getAllTransactions(): kotlinx.coroutines.flow.Flow<List<com.pandu.allocap.data.model.Transaction>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override fun getSumByCategory(category: String) = kotlinx.coroutines.flow.flowOf(0.0)
                override fun getTotalCapital() = kotlinx.coroutines.flow.flowOf(0.0)
                override fun getTransactionsAfter(startTime: Long): kotlinx.coroutines.flow.Flow<List<com.pandu.allocap.data.model.Transaction>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override suspend fun insertTransaction(transaction: com.pandu.allocap.data.model.Transaction) {}
                override suspend fun updateTransaction(transaction: com.pandu.allocap.data.model.Transaction) {}
                override suspend fun deleteTransaction(transaction: com.pandu.allocap.data.model.Transaction) {}
            }),
            onNavigateBack = {}
        )
    }
}
