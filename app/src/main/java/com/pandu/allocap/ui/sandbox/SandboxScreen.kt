package com.pandu.allocap.ui.sandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.ui.components.SimulationSlider
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.PaleSageMint
import com.pandu.allocap.ui.theme.WarmTerracotta
import com.pandu.allocap.ui.theme.AlloCapTheme
import com.pandu.allocap.ui.theme.ColorSavings
import com.pandu.allocap.ui.theme.DeepSpruceMediumOpacity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandboxScreen(
    viewModel: SandboxViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }
    
    val backgroundColor = PaleSageMint
    val spruceColor = DeepSpruce

    if (state.showSuccess) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar("Allocation rules applied successfully")
            viewModel.dismissSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Allocation Sandbox", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Simulate local reallocations risk-free", fontSize = 12.sp, color = spruceColor.copy(alpha = 0.6f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { 
                    Text(
                        "Apply New Rules to Database", 
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ) 
                },
                icon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(24.dp)) },
                onClick = { viewModel.applySettings() },
                containerColor = ColorSavings,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(
                        elevation = if (state.isDirty) 24.dp else 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = ColorSavings,
                        spotColor = ColorSavings
                    ),
                expanded = state.isDirty
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            SimulationSlider(
                label = "Needs (50% target)",
                value = state.needs,
                onValueChange = { viewModel.updateNeeds(it) },
                safeRange = 0.4f..0.6f
            )

            SimulationSlider(
                label = "Wants (30% target)",
                value = state.wants,
                onValueChange = { viewModel.updateWants(it) },
                safeRange = 0.2f..0.4f
            )

            SimulationSlider(
                label = "Savings (20% target)",
                value = state.savings,
                onValueChange = { viewModel.updateSavings(it) },
                safeRange = 0.15f..1.0f // At least 15%
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = spruceColor.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "How it works:",
                        fontWeight = FontWeight.Bold,
                        color = spruceColor,
                        fontSize = 14.sp
                    )
                    Text(
                        "Adjusting one category automatically balances the others to keep your total allocation at 100%. Check health markers for safety.",
                        color = spruceColor.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SandboxPreview() {
    AlloCapTheme {
        SandboxScreen(
            viewModel = SandboxViewModel(object : com.pandu.allocap.data.local.AllocationDao {
                override fun getAllocationSettings() = kotlinx.coroutines.flow.flowOf(com.pandu.allocap.data.model.AllocationSettings(needsPercent = 0.5f, wantsPercent = 0.3f, savingsPercent = 0.2f))
                override suspend fun updateAllocationSettings(settings: com.pandu.allocap.data.model.AllocationSettings) {}
                override fun getAllTransactions(): kotlinx.coroutines.flow.Flow<List<com.pandu.allocap.data.model.Transaction>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override fun getSumByCategory(category: String) = kotlinx.coroutines.flow.flowOf(0.0)
                override fun getTotalCapital() = kotlinx.coroutines.flow.flowOf(0.0)
                override fun getTransactionsAfter(startTime: Long): kotlinx.coroutines.flow.Flow<List<com.pandu.allocap.data.model.Transaction>> = kotlinx.coroutines.flow.flowOf(emptyList())
                override suspend fun insertTransaction(transaction: com.pandu.allocap.data.model.Transaction) {}
                override suspend fun updateTransaction(transaction: com.pandu.allocap.data.model.Transaction) {}
                override suspend fun deleteTransaction(transaction: com.pandu.allocap.data.model.Transaction) {}
            }).apply {
                // We can't easily force state.isDirty here without a real VM or more complex mock
            },
            onNavigateBack = {}
        )
    }
}
