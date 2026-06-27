package com.pandu.allocap.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pandu.allocap.data.model.Transaction
import com.pandu.allocap.ui.components.AllocationChart
import com.pandu.allocap.ui.components.QuickActionGrid
import com.pandu.allocap.ui.components.AddTransactionBottomSheet
import com.pandu.allocap.ui.components.TransactionRow
import com.pandu.allocap.ui.theme.*
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateTo: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    DashboardContent(
        state = state,
        onNavigateTo = onNavigateTo,
        onSaveTransaction = { viewModel.saveTransaction(it) },
        onDeleteTransaction = { viewModel.deleteTransaction(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    state: DashboardState,
    onNavigateTo: (String) -> Unit,
    onSaveTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit = {}
) {
    var showAddTransaction by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DashboardTopBar(state.greeting, state.userName, onNavigateTo)
        },
        containerColor = PaleSageMint
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        ) {
            item {
                CapitalAllocationCard(state)
            }

            if (state.aiAdvice != null) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    AIAssistantCard(advice = state.aiAdvice)
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 18.sp,
                    color = DeepSpruce
                )
                Spacer(modifier = Modifier.height(16.dp))
                QuickActionGrid(onActionClick = { action ->
                    if (action == "add") {
                        showAddTransaction = true
                    } else {
                        onNavigateTo(action)
                    }
                })
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (state.recentTransactions.isEmpty()) {
                item {
                    DashboardEmptyState()
                }
            } else {
                item {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 18.sp,
                        color = DeepSpruce
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

            items(
                    items = state.recentTransactions,
                    key = { it.id }
                ) { transaction ->
                    AnimatedVisibility(
                        visible = true,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                when (it) {
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        onDeleteTransaction(transaction)
                                        true
                                    }
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        onNavigateTo("edit_transaction_${transaction.id}")
                                        false
                                    }
                                    else -> false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> WarmTerracotta
                                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFE57373)
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) 
                                        Alignment.CenterStart else Alignment.CenterEnd
                                ) {
                                    if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color.White)
                                    } else if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.White)
                                    }
                                }
                            }
                        ) {
                            Surface(color = PaleSageMint) {
                                TransactionRow(transaction = transaction)
                            }
                        }
                    }
                    HorizontalDivider(color = DeepSpruce.copy(alpha = 0.05f))
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { onNavigateTo("analytics") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View All Transactions", color = WarmTerracotta, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showAddTransaction) {
        AddTransactionBottomSheet(
            onDismiss = { showAddTransaction = false },
            onSave = { transaction ->
                onSaveTransaction(transaction)
                showAddTransaction = false
            }
        )
    }
}

@Composable
fun DashboardEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No transactions yet.",
            color = DeepSpruce.copy(alpha = 0.5f),
            fontSize = 14.sp
        )
        Text(
            text = "Your financial story starts here.",
            color = DeepSpruce.copy(alpha = 0.3f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun DashboardTopBar(greeting: String, userName: String, onNavigateTo: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$greeting,",
                style = MaterialTheme.typography.labelLarge,
                color = DeepSpruce.copy(alpha = 0.6f)
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 20.sp,
                color = DeepSpruce
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = DeepSpruce
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                onClick = { onNavigateTo("profile") },
                shape = CircleShape,
                color = PremiumTeal,
                modifier = Modifier
                    .size(44.dp)
                    .border(2.dp, ElectricMint.copy(alpha = 0.5f), CircleShape)
                    .shadow(8.dp, CircleShape)
            ) {
                // Here we show the avatar or a simple initial
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = ElectricMint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CapitalAllocationCard(state: DashboardState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = DeepSpruce,
                spotColor = DeepSpruce
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(DeepSpruce, Color(0xFF2C4E4E))
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Allocated Capital",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$${String.format(Locale.US, "%,.2f", state.totalCapital)}",
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    AllocationLegend("Needs", ColorNeeds, state.needsAmount, state.needsActualPercent)
                    AllocationLegend("Wants", ColorWants, state.wantsAmount, state.wantsActualPercent)
                    AllocationLegend("Savings", ColorSavings, state.savingsAmount, state.savingsActualPercent)
                }

                AllocationChart(
                    needsPercent = state.needsActualPercent.coerceIn(0f, 1f),
                    wantsPercent = state.wantsActualPercent.coerceIn(0f, 1f),
                    savingsPercent = state.savingsActualPercent.coerceIn(0f, 1f),
                    modifier = Modifier.size(130.dp)
                )
            }
        }
    }
}

@Composable
fun AIAssistantCard(advice: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Psychology,
                contentDescription = "AI Assistant",
                tint = WarmTerracotta,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = advice,
                style = MaterialTheme.typography.bodyMedium,
                color = DeepSpruce.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    AlloCapTheme {
        DashboardContent(
            state = DashboardState(
                userName = "Pandu",
                totalCapital = 5240.50,
                needsAmount = 2500.0,
                wantsAmount = 1500.0,
                savingsAmount = 1240.50,
                needsActualPercent = 0.48f,
                wantsActualPercent = 0.28f,
                savingsActualPercent = 0.24f,
                needsTarget = 0.5f,
                wantsTarget = 0.3f,
                savingsTarget = 0.2f,
                recentTransactions = listOf(
                    Transaction(id = 1, amount = 1200.0, category = "Savings", description = "Monthly Salary", isIncome = true),
                    Transaction(id = 2, amount = 50.0, category = "Needs", description = "Grocery", isIncome = false),
                    Transaction(id = 3, amount = 30.0, category = "Wants", description = "Movie", isIncome = false)
                ),
                greeting = "Good afternoon",
                aiAdvice = "AI Financial Insight: Your current capital allocation perfectly aligns with your financial strategy."
            ),
            onNavigateTo = {},
            onSaveTransaction = {},
            onDeleteTransaction = {}
        )
    }
}

@Composable
fun AllocationLegend(label: String, color: Color, amount: Double, percent: Float) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = "$${String.format(Locale.US, "%.0f", amount)} (${(percent * 100).toInt()}%)",
            style = MaterialTheme.typography.labelLarge,
            fontSize = 13.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
