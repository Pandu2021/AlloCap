package com.pandu.allocap.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.ui.theme.AlloCapTheme
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.PaleSageMint
import com.pandu.allocap.ui.theme.WarmTerracotta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportData(context, it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.restoreData(context, it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy & Data", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard()

            Spacer(modifier = Modifier.height(16.dp))

            MaintenanceActionCard(
                title = "Export Encrypted Ledger",
                subtitle = "Save a local copy of your financial data",
                icon = Icons.Outlined.FileDownload,
                onClick = { exportLauncher.launch("allocap_backup.json") }
            )

            MaintenanceActionCard(
                title = "Restore Local Data",
                subtitle = "Import your previous ledger from a file",
                icon = Icons.Outlined.FileUpload,
                onClick = { importLauncher.launch(arrayOf("application/json")) }
            )
            
            if (state.error != null) {
                Text(text = state.error!!, color = Color.Red, fontSize = 12.sp)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    AlloCapTheme {
        Box(modifier = Modifier.fillMaxSize().background(PaleSageMint).padding(24.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard()
                MaintenanceActionCard(
                    title = "Export Encrypted Ledger",
                    subtitle = "Save a local copy of your financial data",
                    icon = Icons.Outlined.FileDownload,
                    onClick = { }
                )
                MaintenanceActionCard(
                    title = "Restore Local Data",
                    subtitle = "Import your previous ledger from a file",
                    icon = Icons.Outlined.FileUpload,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun InfoCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = DeepSpruce.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.CloudOff, contentDescription = null, tint = DeepSpruce)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("100% Offline Privacy", fontWeight = FontWeight.Bold, color = DeepSpruce)
                Text(
                    "AlloCap never syncs your data to the cloud. Your capital is strictly your business.",
                    fontSize = 12.sp,
                    color = DeepSpruce.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun MaintenanceActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PaleSageMint, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = WarmTerracotta)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = DeepSpruce)
                Text(subtitle, fontSize = 12.sp, color = DeepSpruce.copy(alpha = 0.6f))
            }
        }
    }
}
