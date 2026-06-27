package com.pandu.allocap

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.pandu.allocap.ui.profile.ProfileScreen
import com.pandu.allocap.ui.profile.ProfileViewModel
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
        val profileViewModel = ProfileViewModel(userRepository)

        scheduleBudgetWorker()

        setContent {
            val navController = rememberNavController()

            AlloCapTheme {
                NavHost(
                    navController = navController,
                    startDestination = "welcome",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { 1000 },
                            animationSpec = tween(500)
                        ) + fadeIn(animationSpec = tween(500))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -1000 },
                            animationSpec = tween(500)
                        ) + fadeOut(animationSpec = tween(500))
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -1000 },
                            animationSpec = tween(500)
                        ) + fadeIn(animationSpec = tween(500))
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { 1000 },
                            animationSpec = tween(500)
                        ) + fadeOut(animationSpec = tween(500))
                    }
                ) {
                    composable("welcome") {
                        WelcomeScreen(
                            viewModel = welcomeViewModel,
                            onUnlockSuccess = {
                                navController.navigate("dashboard") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = dashboardViewModel,
                            onNavigateTo = { action ->
                                when (action) {
                                    "sandbox" -> navController.navigate("sandbox")
                                    "analytics" -> navController.navigate("ledger")
                                    "settings" -> navController.navigate("settings")
                                    "envelope" -> navController.navigate("envelopes")
                                    "profile" -> navController.navigate("profile")
                                }
                            }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            viewModel = profileViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("sandbox") {
                        SandboxScreen(
                            viewModel = sandboxViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("ledger") {
                        LedgerScreen(
                            viewModel = ledgerViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("envelopes") {
                        EnvelopeStatusScreen(
                            viewModel = envelopeViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = { navController.popBackStack() }
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
