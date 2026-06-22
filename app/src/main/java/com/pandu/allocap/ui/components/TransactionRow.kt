package com.pandu.allocap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandu.allocap.data.model.Transaction
import com.pandu.allocap.ui.theme.CharcoalSlate
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.WarmTerracotta
import com.pandu.allocap.ui.theme.ColorIncome
import com.pandu.allocap.ui.theme.ColorExpense
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionRow(transaction: Transaction) {
    val isIncome = transaction.isIncome
    val amountColor = if (isIncome) ColorIncome else ColorExpense
    val formattedAmount = (if (isIncome) "+" else "-") + "$" + String.format(Locale.US, "%.2f", kotlin.math.abs(transaction.amount))
    
    val date = Date(transaction.timestamp)
    val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val timestampStr = formatter.format(date)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryIcon(category = transaction.category)
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.description.ifEmpty { transaction.category },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = CharcoalSlate
            )
            Text(
                text = timestampStr,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 12.sp,
                color = CharcoalSlate.copy(alpha = 0.5f)
            )
        }
        
        Text(
            text = formattedAmount,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}
