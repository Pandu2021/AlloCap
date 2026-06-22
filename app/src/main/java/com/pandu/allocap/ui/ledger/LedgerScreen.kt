package com.pandu.allocap.ui.ledger

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.ui.components.TransactionRow
import com.pandu.allocap.ui.theme.AlloCapTheme
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.PaleSageMint
import com.pandu.allocap.ui.theme.WarmTerracotta
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(
    viewModel: LedgerViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Office Ledger", fontSize = 20.sp, fontWeight = FontWeight.Black) },
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
        ) {
            FilterPills(
                selectedFilter = state.selectedFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SummaryHeader(
                income = state.totalIncome,
                expense = state.totalExpense,
                balance = state.netBalance,
                filter = state.selectedFilter
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.transactions.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.transactions) { transaction ->
                        TransactionRow(transaction = transaction)
                        Divider(color = DeepSpruce.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }
}

@Composable
fun FilterPills(
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DeepSpruce.copy(alpha = 0.05f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeFilter.entries.forEach { filter ->
            val isSelected = selectedFilter == filter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) DeepSpruce else Color.Transparent)
                    .clickable { onFilterSelected(filter) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = if (isSelected) Color.White else DeepSpruce,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun LedgerPreview() {
    val mockTransactions = listOf(
        com.pandu.allocap.data.model.Transaction(id = 1, amount = 45.50, category = "Food", description = "Lunch at Spruce Cafe", isIncome = false),
        com.pandu.allocap.data.model.Transaction(id = 2, amount = 120.00, category = "Transport", description = "Fuel for week", isIncome = false),
        com.pandu.allocap.data.model.Transaction(id = 3, amount = 60.00, category = "Utilities", description = "Internet bill", isIncome = false)
    )

    AlloCapTheme {
        Box(modifier = Modifier.fillMaxSize().background(PaleSageMint).padding(24.dp)) {
            Column {
                FilterPills(selectedFilter = TimeFilter.WEEKLY, onFilterSelected = {})
                Spacer(modifier = Modifier.height(24.dp))
                SummaryHeader(income = 500.0, expense = 225.50, balance = 274.50, filter = TimeFilter.WEEKLY)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(mockTransactions) {
                        TransactionRow(it)
                        Divider(color = DeepSpruce.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryHeader(income: Double, expense: Double, balance: Double, filter: TimeFilter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DeepSpruce)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Financial Summary (${filter.name.lowercase()})",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = "$${String.format(Locale.US, "%,.2f", balance)}",
                style = MaterialTheme.typography.displayLarge,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Income", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                    Text(
                        text = "+$${String.format(Locale.US, "%,.0f", income)}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Expenses", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                    Text(
                        text = "-$${String.format(Locale.US, "%,.0f", expense)}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE57373)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No transactions found for this period.",
            color = DeepSpruce.copy(alpha = 0.5f),
            fontSize = 14.sp
        )
        Text(
            text = "Your financial ledger is awaiting its first entry.",
            color = DeepSpruce.copy(alpha = 0.3f),
            fontSize = 12.sp
        )
    }
}
