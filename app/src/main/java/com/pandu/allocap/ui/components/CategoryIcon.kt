package com.pandu.allocap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pandu.allocap.ui.theme.DeepSpruce
import com.pandu.allocap.ui.theme.WarmTerracotta

@Composable
fun CategoryIcon(category: String, modifier: Modifier = Modifier) {
    val icon = when (category) {
        "Food" -> Icons.Outlined.Restaurant
        "Transport" -> Icons.Outlined.DirectionsCar
        "Utilities" -> Icons.Outlined.Lightbulb
        "Needs" -> Icons.Outlined.Inventory2
        "Wants" -> Icons.Outlined.ShoppingBag
        "Savings" -> Icons.Outlined.Savings
        else -> Icons.Outlined.ReceiptLong
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DeepSpruce.copy(alpha = 0.05f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = category,
            tint = DeepSpruce,
            modifier = Modifier.size(24.dp)
        )
        // Accent dot
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(WarmTerracotta)
        )
    }
}
