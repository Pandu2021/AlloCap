package com.pandu.allocap

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pandu.allocap.data.UserPreferencesRepository
import com.pandu.allocap.data.local.AppDatabase
import com.pandu.allocap.ui.dashboard.DashboardScreen
import com.pandu.allocap.ui.dashboard.DashboardViewModel
import com.pandu.allocap.ui.envelopes.EnvelopeStatusScreen
import com.pandu.allocap.ui.envelopes.EnvelopeViewModel
import com.pandu.allocap.ui.ledger.LedgerScreen
import com.pandu.allocap.ui.ledger.LedgerViewModel
import com.pandu.allocap.ui.sandbox.SandboxScreen
import com.pandu.allocap.ui.sandbox.SandboxViewModel
import com.pandu.allocap.ui.settings.SettingsScreen
import com.pandu.allocap.ui.settings.SettingsViewModel
import com.pandu.allocap.ui.theme.AlloCapTheme
import com.pandu.allocap.ui.welcome.WelcomeScreen
import com.pandu.allocap.ui.welcome.WelcomeViewModel
import com.pandu.allocap.worker.BudgetWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual DI
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserPreferencesRepository(applicationContext)
        val dao = appDatabase.allocationDao()
        
        val welcomeViewModel = WelcomeViewModel(userRepository)
        val dashboardViewModel = DashboardViewModel(dao, userRepository)
        val sandboxViewModel = SandboxViewModel(dao)
        val ledgerViewModel = LedgerViewModel(dao)
        val settingsViewModel = SettingsViewModel(dao)
        val envelopeViewModel = EnvelopeViewModel(dao)

        scheduleBudgetWorker()

        setContent {
            var currentScreen by remember { mutableStateOf("welcome") }

            AlloCapTheme {
                when (currentScreen) {
                    "welcome" -> WelcomeScreen(
                        viewModel = welcomeViewModel,
                        onUnlockSuccess = {
                            currentScreen = "dashboard"
                        }
                    )
                    "dashboard" -> {
                        BackHandler { currentScreen = "welcome" }
                        DashboardScreen(
                            viewModel = dashboardViewModel,
                            onNavigateTo = { action ->
                                when (action) {
                                    "sandbox" -> currentScreen = "sandbox"
                                    "analytics" -> currentScreen = "ledger"
                                    "settings" -> currentScreen = "settings"
                                    "envelope" -> currentScreen = "envelopes"
                                    else -> {
                                        if (action.startsWith("edit_transaction_")) {
                                            Toast.makeText(this, "Opening Edit Mode...", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, "Action: $action", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }
                    "sandbox" -> {
                        BackHandler { currentScreen = "dashboard" }
                        SandboxScreen(
                            viewModel = sandboxViewModel,
                            onNavigateBack = {
                                currentScreen = "dashboard"
                            }
                        )
                    }
                    "ledger" -> {
                        BackHandler { currentScreen = "dashboard" }
                        LedgerScreen(
                            viewModel = ledgerViewModel,
                            onNavigateBack = {
                                currentScreen = "dashboard"
                            }
                        )
                    }
                    "envelopes" -> {
                        BackHandler { currentScreen = "dashboard" }
                        EnvelopeStatusScreen(
                            viewModel = envelopeViewModel,
                            onNavigateBack = {
                                currentScreen = "dashboard"
                            }
                        )
                    }
                    "settings" -> {
                        BackHandler { currentScreen = "dashboard" }
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = {
                                currentScreen = "dashboard"
                            }
                        )
                    }
                }
            }
        }
    }

    private fun scheduleBudgetWorker() {
        val workRequest = PeriodicWorkRequestBuilder<BudgetWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "budget_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
