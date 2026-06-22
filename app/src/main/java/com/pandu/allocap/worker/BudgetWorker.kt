package com.pandu.allocap.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pandu.allocap.data.local.AppDatabase
import com.pandu.allocap.ui.utils.NotificationHelper
import kotlinx.coroutines.flow.first

class BudgetWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.allocationDao()
        val notificationHelper = NotificationHelper(applicationContext)

        val settings = dao.getAllocationSettings().first() ?: return Result.success()
        val totalCapital = dao.getTotalCapital().first() ?: 0.0
        
        if (totalCapital <= 0) return Result.success()

        // Check Needs
        val needsSpent = dao.getSumByCategory("Needs").first() ?: 0.0
        val needsLimit = totalCapital * settings.needsPercent
        if (needsSpent >= needsLimit * 0.9) {
            notificationHelper.sendBudgetAlert("Needs")
        }

        // Check Wants
        val wantsSpent = dao.getSumByCategory("Wants").first() ?: 0.0
        val wantsLimit = totalCapital * settings.wantsPercent
        if (wantsSpent >= wantsLimit * 0.9) {
            notificationHelper.sendBudgetAlert("Wants")
        }

        return Result.success()
    }
}
