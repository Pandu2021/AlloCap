package com.pandu.allocap.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pandu.allocap.data.local.AllocationDao
import com.pandu.allocap.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

data class SettingsState(
    val exportSuccess: Boolean = false,
    val restoreSuccess: Boolean = false,
    val error: String? = null
)

class SettingsViewModel(
    private val allocationDao: AllocationDao
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state
    private val gson = Gson()

    fun exportData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val transactions = allocationDao.getAllTransactions().first()
                val json = gson.toJson(transactions)
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(json.toByteArray())
                }
                _state.value = SettingsState(exportSuccess = true)
            } catch (e: Exception) {
                _state.value = SettingsState(error = "Export failed: ${e.message}")
            }
        }
    }

    fun restoreData(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val json = reader.readText()
                val transactions = gson.fromJson(json, Array<Transaction>::class.java).toList()
                
                // For a safe restore, we'd typically clear the DB or merge. 
                // Here we just insert all (Room will handle IDs if we use 0)
                transactions.forEach {
                    allocationDao.insertTransaction(it.copy(id = 0))
                }
                
                _state.value = SettingsState(restoreSuccess = true)
            } catch (e: Exception) {
                _state.value = SettingsState(error = "Restore failed: ${e.message}")
            }
        }
    }
}
