package com.pandu.allocap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pandu.allocap.data.model.AllocationSettings
import com.pandu.allocap.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AllocationDao {
    @Query("SELECT * FROM allocation_settings WHERE id = 1")
    fun getAllocationSettings(): Flow<AllocationSettings?>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun updateAllocationSettings(settings: AllocationSettings)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT SUM(CASE WHEN isIncome = 1 THEN amount ELSE -amount END) FROM transactions WHERE category = :category")
    fun getSumByCategory(category: String): Flow<Double?>

    @Query("SELECT SUM(CASE WHEN isIncome = 1 THEN amount ELSE -amount END) FROM transactions")
    fun getTotalCapital(): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getTransactionsAfter(startTime: Long): Flow<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @androidx.room.Update
    suspend fun updateTransaction(transaction: Transaction)

    @androidx.room.Delete
    suspend fun deleteTransaction(transaction: Transaction)
}
